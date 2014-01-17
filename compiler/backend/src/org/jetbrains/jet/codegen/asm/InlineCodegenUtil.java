/*
 * Copyright 2010-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.codegen.asm;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.asm4.ClassReader;
import org.jetbrains.asm4.ClassVisitor;
import org.jetbrains.asm4.MethodVisitor;
import org.jetbrains.asm4.Opcodes;
import org.jetbrains.asm4.tree.MethodNode;
import org.jetbrains.jet.codegen.state.GenerationState;
import org.jetbrains.jet.descriptors.serialization.JavaProtoBuf;
import org.jetbrains.jet.descriptors.serialization.ProtoBuf;
import org.jetbrains.jet.descriptors.serialization.descriptors.DeserializedSimpleFunctionDescriptor;
import org.jetbrains.jet.lang.descriptors.*;
import org.jetbrains.jet.lang.resolve.DescriptorUtils;
import org.jetbrains.jet.lang.resolve.java.PackageClassUtils;
import org.jetbrains.jet.lang.resolve.kotlin.ClassFileFinder;
import org.jetbrains.jet.lang.resolve.kotlin.VirtualFileFinder;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.jet.lang.resolve.name.Name;

import java.io.IOException;
import java.io.InputStream;

import static org.jetbrains.jet.lang.resolve.DescriptorUtils.getFqName;

public class InlineCodegenUtil {

    public final static int API = Opcodes.ASM4;

    public final static String INVOKE = "invoke";

    @Nullable
    public static MethodNode getMethodNode(
            InputStream classData,
            final String methodName,
            final String methodDescriptor
    ) throws ClassNotFoundException, IOException {
        ClassReader cr = new ClassReader(classData);
        final MethodNode[] methodNode = new MethodNode[1];
        cr.accept(new ClassVisitor(API) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if (methodName.equals(name) && methodDescriptor.equals(desc)) {
                    return methodNode[0] = new MethodNode(access, name, desc, signature, exceptions);
                }
                return null;
            }
        }, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        return methodNode[0];
    }


    @NotNull
    public static VirtualFile getVirtualFileForCallable(DeserializedSimpleFunctionDescriptor deserializedDescriptor, GenerationState state) {
        VirtualFile file = null;
        DeclarationDescriptor parentDeclatation = deserializedDescriptor.getContainingDeclaration();
        if (parentDeclatation instanceof PackageFragmentDescriptor) {
            ProtoBuf.Callable proto = deserializedDescriptor.getFunctionProto();
            if (proto.hasExtension(JavaProtoBuf.implClassName)) {
                Name name = deserializedDescriptor.getNameResolver().getName(proto.getExtension(JavaProtoBuf.implClassName));
                FqName namespaceFqName =
                        PackageClassUtils.getPackageClassFqName(((PackageFragmentDescriptor) parentDeclatation).getFqName()).parent().child(
                                name);
                file = findVirtualFile(state.getProject(), namespaceFqName, true);
            } else {
                assert false : "Function in namespace should have implClassName property in proto: " + deserializedDescriptor;
            }
        } else {
            file = findVirtualFileContainingDescriptor(state.getProject(), deserializedDescriptor);
        }

        if (file == null) {
            throw new RuntimeException("Couldn't find declaration file for " + deserializedDescriptor.getName());
        }

        return file;
    }

    @Nullable
    public static VirtualFile findVirtualFile(@NotNull Project project, @NotNull FqName containerFqName, boolean onlyKotlin) {
        ClassFileFinder fileFinder = ServiceManager.getService(project, ClassFileFinder.class);
        return fileFinder.find(containerFqName.asString().replace('.', '/'));
    }

    //TODO: navigate to inner classes
    @Nullable
    private static FqName getContainerFqName(@NotNull DeclarationDescriptor referencedDescriptor) {
        ClassOrNamespaceDescriptor
                containerDescriptor = DescriptorUtils.getParentOfType(referencedDescriptor, ClassOrNamespaceDescriptor.class, false);
        if (containerDescriptor instanceof PackageFragmentDescriptor) {
            return PackageClassUtils.getPackageClassFqName(getFqName(containerDescriptor).toSafe());
        }
        if (containerDescriptor instanceof ClassDescriptor) {
            ClassKind classKind = ((ClassDescriptor) containerDescriptor).getKind();
            if (classKind == ClassKind.CLASS_OBJECT || classKind == ClassKind.ENUM_ENTRY) {
                return getContainerFqName(containerDescriptor.getContainingDeclaration());
            }
            return getFqName(containerDescriptor).toSafe();
        }
        return null;
    }

    @Nullable
    private static VirtualFile findVirtualFileContainingDescriptor(
            @NotNull Project project,
            @NotNull DeclarationDescriptor referencedDescriptor
    ) {
        FqName containerFqName = getContainerFqName(referencedDescriptor);
        if (containerFqName == null) {
            return null;
        }
        return findVirtualFile(project, containerFqName, true);
    }


    public static boolean isInvokeOnInlinable(String owner, String name) {
        return INVOKE.equals(name) && /*TODO: check type*/owner.contains("Function");
    }

    public static boolean isFunctionConstructorCall(@NotNull String internalName, @NotNull String name) {
        if (!"<init>".equals(name)) {
            return false;
        }

        return isFunctionLiteralClass(internalName);
    }

    public static boolean isFunctionLiteralClass(String internalName) {
        String shortName = getLastNamePart(internalName);
        int index = shortName.lastIndexOf("$");

        if (index < 0) {
            return false;
        }

        String suffix = shortName.substring(index + 1);
        for (char c : suffix.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    @NotNull
    private static String getLastNamePart(@NotNull String internalName) {
        int index = internalName.lastIndexOf("/");
        return index < 0 ? internalName : internalName.substring(index + 1);
    }

    public static boolean isInitCallOfFunction(String owner, String name) {
        return "<init>".equals(name);
    }
}
