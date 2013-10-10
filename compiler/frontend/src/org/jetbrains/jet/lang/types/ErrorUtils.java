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

package org.jetbrains.jet.lang.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.ModuleConfiguration;
import org.jetbrains.jet.lang.PlatformToKotlinClassMap;
import org.jetbrains.jet.lang.descriptors.*;
import org.jetbrains.jet.lang.descriptors.annotations.AnnotationDescriptor;
import org.jetbrains.jet.lang.descriptors.impl.ConstructorDescriptorImpl;
import org.jetbrains.jet.lang.descriptors.impl.PropertyDescriptorImpl;
import org.jetbrains.jet.lang.descriptors.impl.TypeParameterDescriptorImpl;
import org.jetbrains.jet.lang.descriptors.impl.ValueParameterDescriptorImpl;
import org.jetbrains.jet.lang.resolve.ImportPath;
import org.jetbrains.jet.lang.resolve.name.LabelName;
import org.jetbrains.jet.lang.resolve.name.Name;
import org.jetbrains.jet.lang.resolve.scopes.JetScope;
import org.jetbrains.jet.lang.types.error.ErrorClassDescriptor;
import org.jetbrains.jet.lang.types.error.ErrorSimpleFunctionDescriptorImpl;
import org.jetbrains.jet.lang.types.lang.KotlinBuiltIns;

import java.util.*;

import static org.jetbrains.jet.lang.types.TypeUtils.noExpectedType;

public class ErrorUtils {

    private static final ModuleDescriptor ERROR_MODULE;
    static {
        ModuleDescriptorImpl module = new ModuleDescriptorImpl(
                Name.special("<ERROR MODULE>"),
                Collections.<ImportPath>emptyList(),
                PlatformToKotlinClassMap.EMPTY
        );
        module.setModuleConfiguration(ModuleConfiguration.EMPTY);
        ERROR_MODULE = module;
    }

    public static boolean containsErrorType(@NotNull FunctionDescriptor function) {
        if (containsErrorType(function.getReturnType())) {
            return true;
        }
        ReceiverParameterDescriptor receiverParameter = function.getReceiverParameter();
        if (receiverParameter != null && containsErrorType(receiverParameter.getType())) {
            return true;
        }
        for (ValueParameterDescriptor parameter : function.getValueParameters()) {
            if (containsErrorType(parameter.getType())) {
                return true;
            }
        }
        for (TypeParameterDescriptor parameter : function.getTypeParameters()) {
            for (JetType upperBound : parameter.getUpperBounds()) {
                if (containsErrorType(upperBound)) {
                    return true;
                }
            }
        }

        return false;
    }


    public static class ErrorScope implements JetScope {

        private final String debugMessage;

        private ErrorScope(String debugMessage) {
            this.debugMessage = debugMessage;
        }

        @Override
        public ClassifierDescriptor getClassifier(@NotNull Name name) {
            return ERROR_CLASS;
        }

        @Override
        public ClassDescriptor getObjectDescriptor(@NotNull Name name) {
            return ERROR_CLASS;
        }

        @NotNull
        @Override
        public Set<ClassDescriptor> getObjectDescriptors() {
            return Collections.emptySet();
        }

        @NotNull
        @Override
        public Set<VariableDescriptor> getProperties(@NotNull Name name) {
            return ERROR_PROPERTY_GROUP;
        }

        @Override
        public VariableDescriptor getLocalVariable(@NotNull Name name) {
            return ERROR_PROPERTY;
        }

        @Override
        public NamespaceDescriptor getNamespace(@NotNull Name name) {
            return null; // TODO : review
        }

        @NotNull
        @Override
        public List<ReceiverParameterDescriptor> getImplicitReceiversHierarchy() {
            return Collections.emptyList();
        }

        @NotNull
        @Override
        public Set<FunctionDescriptor> getFunctions(@NotNull Name name) {
            return Collections.<FunctionDescriptor>singleton(createErrorFunction(this));
        }

        @NotNull
        @Override
        public DeclarationDescriptor getContainingDeclaration() {
            return ERROR_MODULE;
        }

        @NotNull
        @Override
        public Collection<DeclarationDescriptor> getDeclarationsByLabel(LabelName labelName) {
            return Collections.emptyList();
        }

        @Override
        public PropertyDescriptor getPropertyByFieldReference(@NotNull Name fieldName) {
            return null; // TODO : review
        }

        @NotNull
        @Override
        public Collection<DeclarationDescriptor> getAllDescriptors() {
            return Collections.emptyList();
        }

        @NotNull
        @Override
        public Collection<DeclarationDescriptor> getOwnDeclaredDescriptors() {
            return Collections.emptyList();
        }

        @Override
        public String toString() {
            return "ErrorScope{" + debugMessage + '}';
        }
    }

    private static class ThrowingScope implements JetScope {
        private final String debugMessage;

        private ThrowingScope(String message) {
            debugMessage = message;
        }

        @Nullable
        @Override
        public ClassifierDescriptor getClassifier(@NotNull Name name) {
            throw new IllegalStateException();
        }

        @Nullable
        @Override
        public ClassDescriptor getObjectDescriptor(@NotNull Name name) {
            throw new IllegalStateException();
        }

        @NotNull
        @Override
        public Collection<ClassDescriptor> getObjectDescriptors() {
            throw new IllegalStateException();
        }

        @Nullable
        @Override
        public NamespaceDescriptor getNamespace(@NotNull Name name) {
            throw new IllegalStateException();
        }

        @NotNull
        @Override
        public Collection<VariableDescriptor> getProperties(@NotNull Name name) {
            throw new IllegalStateException();
        }

        @Nullable
        @Override
        public VariableDescriptor getLocalVariable(@NotNull Name name) {
            throw new IllegalStateException();
        }

        @NotNull
        @Override
        public Collection<FunctionDescriptor> getFunctions(@NotNull Name name) {
            throw new IllegalStateException();
        }

        @NotNull
        @Override
        public DeclarationDescriptor getContainingDeclaration() {
            return ERROR_MODULE;
        }

        @NotNull
        @Override
        public Collection<DeclarationDescriptor> getDeclarationsByLabel(@NotNull LabelName labelName) {
            throw new IllegalStateException();
        }

        @Nullable
        @Override
        public PropertyDescriptor getPropertyByFieldReference(@NotNull Name fieldName) {
            throw new IllegalStateException();
        }

        @NotNull
        @Override
        public Collection<DeclarationDescriptor> getAllDescriptors() {
            throw new IllegalStateException();
        }

        @NotNull
        @Override
        public List<ReceiverParameterDescriptor> getImplicitReceiversHierarchy() {
            throw new IllegalStateException();
        }

        @NotNull
        @Override
        public Collection<DeclarationDescriptor> getOwnDeclaredDescriptors() {
            throw new IllegalStateException();
        }

        @Override
        public String toString() {
            return "ThrowingScope{" + debugMessage + '}';
        }
    }

    private static final ErrorClassDescriptor ERROR_CLASS = new ErrorClassDescriptor("");

    private static final class ErrorTypeConstructor extends TypeConstructorImpl {
        private ErrorTypeConstructor(
                @Nullable ClassifierDescriptor classifierDescriptor,
                @NotNull List<AnnotationDescriptor> annotations,
                boolean sealed,
                @NotNull String debugName,
                @NotNull List<? extends TypeParameterDescriptor> parameters,
                @NotNull Collection<JetType> supertypes
        ) {
            super(classifierDescriptor, annotations, sealed, debugName, parameters, supertypes);
        }
    }

    private static final Set<ConstructorDescriptor> ERROR_CONSTRUCTOR_GROUP = Collections.singleton(createErrorConstructor(0, Collections.<JetType>emptyList()));

    private static final ConstructorDescriptor ERROR_CONSTRUCTOR = new ConstructorDescriptorImpl(ERROR_CLASS, Collections.<AnnotationDescriptor>emptyList(), true);

    static {
        ERROR_CLASS.initializeErrorClass();
    }
    @NotNull
    public static Set<ConstructorDescriptor> getErrorConstructorGroup() {
        return ERROR_CONSTRUCTOR_GROUP;
    }

    public static ConstructorDescriptor getErrorConstructor() {
        return ERROR_CONSTRUCTOR;
    }

    public static JetScope createErrorScope(String debugMessage) {
        return createErrorScope(debugMessage, false);
    }

    public static JetScope createErrorScope(String debugMessage, boolean throwExceptions) {
        if (throwExceptions) {
            return new ThrowingScope(debugMessage);
        }
        return new ErrorScope(debugMessage);
    }

    private static final JetType ERROR_PROPERTY_TYPE = createErrorType("<ERROR PROPERTY TYPE>");
    private static final VariableDescriptor ERROR_PROPERTY = new PropertyDescriptorImpl(
            ERROR_CLASS,
            Collections.<AnnotationDescriptor>emptyList(),
            Modality.OPEN,
            Visibilities.INTERNAL,
            true,
            null,
            ReceiverParameterDescriptor.NO_RECEIVER_PARAMETER,
            Name.special("<ERROR PROPERTY>"),
            ERROR_PROPERTY_TYPE,
            CallableMemberDescriptor.Kind.DECLARATION);
    private static final Set<VariableDescriptor> ERROR_PROPERTY_GROUP = Collections.singleton(ERROR_PROPERTY);

    private static SimpleFunctionDescriptor createErrorFunction(ErrorScope ownerScope) {
        ErrorSimpleFunctionDescriptorImpl function = new ErrorSimpleFunctionDescriptorImpl(ownerScope);
        function.initialize(
                null,
                ReceiverParameterDescriptor.NO_RECEIVER_PARAMETER,
                Collections.<TypeParameterDescriptorImpl>emptyList(), // TODO
                Collections.<ValueParameterDescriptor>emptyList(), // TODO
                createErrorType("<ERROR FUNCTION RETURN TYPE>"),
                Modality.OPEN,
                Visibilities.INTERNAL,
                /*isInline = */ false
        );
        return function;
    }

    public static ConstructorDescriptor createErrorConstructor(int typeParameterCount, List<JetType> positionedValueParameterTypes) {
        ConstructorDescriptorImpl r = new ConstructorDescriptorImpl(ERROR_CLASS, Collections.<AnnotationDescriptor>emptyList(), false);
        r.initialize(
                Collections.<TypeParameterDescriptor>emptyList(), // TODO
                Collections.<ValueParameterDescriptor>emptyList(), // TODO
                Visibilities.INTERNAL
        );
        r.setReturnType(createErrorType("<ERROR RETURN TYPE>"));
        return r;
    }

    private static final JetType ERROR_PARAMETER_TYPE = createErrorType("<ERROR VALUE_PARAMETER TYPE>");
    private static List<ValueParameterDescriptor> getValueParameters(FunctionDescriptor functionDescriptor, List<JetType> argumentTypes) {
        List<ValueParameterDescriptor> result = new ArrayList<ValueParameterDescriptor>();
        for (int i = 0, argumentTypesSize = argumentTypes.size(); i < argumentTypesSize; i++) {
            result.add(new ValueParameterDescriptorImpl(
                    functionDescriptor,
                    i,
                    Collections.<AnnotationDescriptor>emptyList(),
                    Name.special("<ERROR VALUE_PARAMETER>"),
                    ERROR_PARAMETER_TYPE,
                    false,
                    null));
        }
        return result;
    }

    @NotNull
    public static JetType createErrorType(String debugMessage) {
        return createErrorType(debugMessage, createErrorScope(debugMessage));
    }

    private static JetType createErrorType(String debugMessage, JetScope memberScope) {
        return createErrorTypeWithCustomDebugName(memberScope, "[ERROR : " + debugMessage + "]");
    }

    @NotNull
    public static JetType createErrorTypeWithCustomDebugName(String debugName) {
        return createErrorTypeWithCustomDebugName(createErrorScope(debugName), debugName);
    }

    private static JetType createErrorTypeWithCustomDebugName(JetScope memberScope, String debugName) {
        TypeConstructorImpl constructor =
                new ErrorTypeConstructor(ERROR_CLASS, Collections.<AnnotationDescriptor>emptyList(), false, debugName,
                                        Collections.<TypeParameterDescriptorImpl>emptyList(),
                                        Collections.singleton(KotlinBuiltIns.getInstance().getAnyType()));
        return new ErrorTypeImpl(constructor, memberScope);
    }

    public static JetType createWrongVarianceErrorType(TypeProjection value) {
        return createErrorType(value + " is not allowed here", value.getType().getMemberScope());
    }

    @NotNull
    public static ClassDescriptor getErrorClass() {
        return ERROR_CLASS;
    }

    public static boolean isError(@NotNull TypeConstructor typeConstructor) {
        return typeConstructor == ERROR_CLASS.getTypeConstructor() || typeConstructor instanceof ErrorTypeConstructor;
    }

    public static boolean isErrorType(@NotNull JetType type) {
        return !noExpectedType(type) && !(type instanceof NamespaceType) &&
               (
                    (type instanceof DeferredType && (((DeferredType) type).getActualType() == null
                                                      || isErrorType(((DeferredType) type).getActualType()))) ||
                    type instanceof ErrorTypeImpl ||
                    isError(type.getConstructor())
               );
    }

    public static boolean containsErrorType(@Nullable JetType type) {
        if (type == null) return false;
        if (type instanceof NamespaceType) return false;
        if (isErrorType(type)) return true;
        for (TypeProjection projection : type.getArguments()) {
            if (containsErrorType(projection.getType())) return true;
        }
        return false;
    }

    public static boolean isError(@NotNull DeclarationDescriptor candidate) {
        return isErrorClass(candidate) || isErrorClass(candidate.getContainingDeclaration()) || candidate == ERROR_MODULE;
    }

    private static boolean isErrorClass(@Nullable DeclarationDescriptor candidate) {
        return candidate instanceof ErrorClassDescriptor;
    }

    @NotNull
    public static ErrorClassDescriptor createErrorClass(@NotNull String debugMessage) {
        ErrorClassDescriptor result = new ErrorClassDescriptor(debugMessage);
        result.initializeErrorClass();
        return result;
    }

    private static class ErrorTypeImpl implements JetType {

        private final TypeConstructor constructor;

        private final JetScope memberScope;
        private ErrorTypeImpl(TypeConstructor constructor, JetScope memberScope) {
            this.constructor = constructor;
            this.memberScope = memberScope;
        }

        @NotNull
        @Override
        public TypeConstructor getConstructor() {
            return constructor;
        }

        @NotNull
        @Override
        public List<TypeProjection> getArguments() {
            return Collections.emptyList();
        }

        @Override
        public boolean isNullable() {
            return false;
        }

        @NotNull
        @Override
        public JetScope getMemberScope() {
            return memberScope;
        }

        @Override
        public List<AnnotationDescriptor> getAnnotations() {
            return Collections.emptyList();
        }

        @Override
        public String toString() {
            return constructor.toString();
        }
    }

    public static ModuleDescriptor getErrorModule() {
        return ERROR_MODULE;
    }

    private ErrorUtils() {}
}