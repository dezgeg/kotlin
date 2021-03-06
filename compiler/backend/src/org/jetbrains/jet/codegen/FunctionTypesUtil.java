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

package org.jetbrains.jet.codegen;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.asm4.Type;
import org.jetbrains.jet.lang.descriptors.*;
import org.jetbrains.jet.lang.descriptors.impl.MutableClassDescriptor;
import org.jetbrains.jet.lang.descriptors.impl.MutablePackageFragmentDescriptor;
import org.jetbrains.jet.lang.resolve.ImportPath;
import org.jetbrains.jet.lang.resolve.java.mapping.JavaToKotlinClassMap;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.jet.lang.resolve.name.Name;
import org.jetbrains.jet.lang.types.JetType;
import org.jetbrains.jet.lang.types.JetTypeImpl;
import org.jetbrains.jet.lang.types.TypeProjection;
import org.jetbrains.jet.lang.types.TypeProjectionImpl;
import org.jetbrains.jet.lang.types.lang.KotlinBuiltIns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jetbrains.jet.lang.types.lang.KotlinBuiltIns.BUILT_INS_PACKAGE_FQ_NAME;

public class FunctionTypesUtil {
    private static final List<ClassDescriptor> FUNCTIONS;
    private static final List<ClassDescriptor> EXTENSION_FUNCTIONS;
    private static final List<ClassDescriptor> K_FUNCTIONS;
    private static final List<ClassDescriptor> K_MEMBER_FUNCTIONS;
    private static final List<ClassDescriptor> K_EXTENSION_FUNCTIONS;

    private static final ImmutableMap<ClassDescriptor, ClassDescriptor> FUNCTION_TO_IMPL;

    static {
        int n = KotlinBuiltIns.FUNCTION_TRAIT_COUNT;
        FUNCTIONS = new ArrayList<ClassDescriptor>(n);
        EXTENSION_FUNCTIONS = new ArrayList<ClassDescriptor>(n);
        K_FUNCTIONS = new ArrayList<ClassDescriptor>(n);
        K_MEMBER_FUNCTIONS = new ArrayList<ClassDescriptor>(n);
        K_EXTENSION_FUNCTIONS = new ArrayList<ClassDescriptor>(n);

        ModuleDescriptor module = new ModuleDescriptorImpl(Name.special("<fake module for functions impl>"),
                                                           Collections.<ImportPath>emptyList(), JavaToKotlinClassMap.getInstance());
        MutablePackageFragmentDescriptor kotlin = new MutablePackageFragmentDescriptor(module, new FqName("kotlin"));
        MutablePackageFragmentDescriptor reflect = new MutablePackageFragmentDescriptor(module, new FqName("kotlin.reflect"));

        KotlinBuiltIns builtIns = KotlinBuiltIns.getInstance();
        for (int i = 0; i < n; i++) {
            createFunctionImpl(FUNCTIONS, kotlin, "FunctionImpl" + i, builtIns.getFunction(i));
            createFunctionImpl(EXTENSION_FUNCTIONS, kotlin, "ExtensionFunctionImpl" + i, builtIns.getExtensionFunction(i));
            createFunctionImpl(K_FUNCTIONS, reflect, "KFunctionImpl" + i, builtIns.getKFunction(i));
            createFunctionImpl(K_MEMBER_FUNCTIONS, reflect, "KMemberFunctionImpl" + i, builtIns.getKMemberFunction(i));
            createFunctionImpl(K_EXTENSION_FUNCTIONS, reflect, "KExtensionFunctionImpl" + i, builtIns.getKExtensionFunction(i));
        }

        ImmutableMap.Builder<ClassDescriptor, ClassDescriptor> builder = ImmutableMap.builder();
        for (int i = 0; i < n; i++) {
            builder.put(builtIns.getKFunction(i), K_FUNCTIONS.get(i));
            builder.put(builtIns.getKMemberFunction(i), K_MEMBER_FUNCTIONS.get(i));
            builder.put(builtIns.getKExtensionFunction(i), K_EXTENSION_FUNCTIONS.get(i));
        }
        FUNCTION_TO_IMPL = builder.build();
    }

    private FunctionTypesUtil() {
    }


    @Nullable
    public static ClassDescriptor functionTypeToImpl(@NotNull JetType functionType) {
        //noinspection SuspiciousMethodCalls
        return FUNCTION_TO_IMPL.get(functionType.getConstructor().getDeclarationDescriptor());
    }

    @NotNull
    public static JetType getSuperTypeForClosure(@NotNull FunctionDescriptor descriptor, boolean kFunction) {
        int arity = descriptor.getValueParameters().size();

        ReceiverParameterDescriptor receiverParameter = descriptor.getReceiverParameter();
        ReceiverParameterDescriptor expectedThisObject = descriptor.getExpectedThisObject();

        List<TypeProjection> typeArguments = new ArrayList<TypeProjection>(arity + 2);
        if (receiverParameter != null) {
            typeArguments.add(new TypeProjectionImpl(receiverParameter.getType()));
        }
        else if (kFunction && expectedThisObject != null) {
            typeArguments.add(new TypeProjectionImpl(expectedThisObject.getType()));
        }

        for (ValueParameterDescriptor parameter : descriptor.getValueParameters()) {
            typeArguments.add(new TypeProjectionImpl(parameter.getType()));
        }

        typeArguments.add(new TypeProjectionImpl(descriptor.getReturnType()));

        ClassDescriptor classDescriptor;
        if (kFunction) {
            if (expectedThisObject != null) {
                classDescriptor = K_MEMBER_FUNCTIONS.get(arity);
            }
            else if (receiverParameter != null) {
                classDescriptor = K_EXTENSION_FUNCTIONS.get(arity);
            }
            else {
                classDescriptor = K_FUNCTIONS.get(arity);
            }
        }
        else {
            if (receiverParameter != null) {
                classDescriptor = EXTENSION_FUNCTIONS.get(arity);
            }
            else {
                classDescriptor = FUNCTIONS.get(arity);
            }
        }

        return new JetTypeImpl(
                classDescriptor.getDefaultType().getAnnotations(),
                classDescriptor.getTypeConstructor(),
                false,
                typeArguments,
                classDescriptor.getMemberScope(typeArguments)
        );
    }

    private static void createFunctionImpl(
            @NotNull List<ClassDescriptor> result,
            @NotNull PackageFragmentDescriptor containingDeclaration,
            @NotNull String name,
            @NotNull ClassDescriptor functionInterface
    ) {
        MutableClassDescriptor functionImpl = new MutableClassDescriptor(
                containingDeclaration,
                containingDeclaration.getMemberScope(),
                ClassKind.CLASS,
                false,
                Name.identifier(name)
        );
        functionImpl.setModality(Modality.FINAL);
        functionImpl.setVisibility(Visibilities.PUBLIC);
        functionImpl.setTypeParameterDescriptors(functionInterface.getDefaultType().getConstructor().getParameters());
        functionImpl.createTypeConstructor();

        result.add(functionImpl);
    }

    @NotNull
    public static Type getFunctionTraitClassName(@NotNull FunctionDescriptor descriptor) {
        int paramCount = descriptor.getValueParameters().size();
        if (descriptor.getReceiverParameter() != null) {
            return Type.getObjectType(BUILT_INS_PACKAGE_FQ_NAME + "/ExtensionFunction" + paramCount);
        }
        else {
            return Type.getObjectType(BUILT_INS_PACKAGE_FQ_NAME + "/Function" + paramCount);
        }
    }

    @NotNull
    public static Type getFunctionImplType(@NotNull FunctionDescriptor descriptor) {
        int paramCount = descriptor.getValueParameters().size();
        if (descriptor.getReceiverParameter() != null) {
            return Type.getObjectType("kotlin/ExtensionFunctionImpl" + paramCount);
        }
        else {
            return Type.getObjectType("kotlin/FunctionImpl" + paramCount);
        }
    }
}
