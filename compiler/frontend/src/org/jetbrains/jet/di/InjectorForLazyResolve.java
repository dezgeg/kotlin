/*
 * Copyright 2010-2014 JetBrains s.r.o.
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

package org.jetbrains.jet.di;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.PlatformToKotlinClassMap;
import org.jetbrains.jet.lang.descriptors.ModuleDescriptorImpl;
import org.jetbrains.jet.lang.psi.JetImportsFactory;
import org.jetbrains.jet.lang.resolve.*;
import org.jetbrains.jet.lang.resolve.calls.*;
import org.jetbrains.jet.lang.resolve.lazy.ResolveSession;
import org.jetbrains.jet.lang.resolve.lazy.ScopeProvider;
import org.jetbrains.jet.lang.resolve.lazy.declarations.FileBasedDeclarationProviderFactory;
import org.jetbrains.jet.lang.resolve.lazy.declarations.FileBasedDeclarationProviderFactory.FileBaseDeclarationConfiguration;
import org.jetbrains.jet.lang.types.expressions.ExpressionTypingServices;
import org.jetbrains.jet.storage.LockBasedLazyResolveStorageManager;
import org.jetbrains.jet.storage.LockBasedStorageManager;

import javax.annotation.PreDestroy;

/* This file is generated by org.jetbrains.jet.generators.injectors.GenerateInjectors. DO NOT EDIT! */
@SuppressWarnings("ALL")
public class InjectorForLazyResolve {
    
    private final Project project;
    private final FileBaseDeclarationConfiguration fileBaseDeclarationConfiguration;
    private final ModuleDescriptorImpl moduleDescriptor;
    private final BindingTrace bindingTrace;
    private final ResolveSession resolveSession;
    private final FileBasedDeclarationProviderFactory fileBasedDeclarationProviderFactory;
    private final LockBasedLazyResolveStorageManager lockBasedLazyResolveStorageManager;
    private final CallResolverExtensionProvider callResolverExtensionProvider;
    private final PlatformToKotlinClassMap platformToKotlinClassMap;
    private final AnnotationResolver annotationResolver;
    private final CallResolver callResolver;
    private final ArgumentTypeResolver argumentTypeResolver;
    private final ExpressionTypingServices expressionTypingServices;
    private final CallExpressionResolver callExpressionResolver;
    private final DescriptorResolver descriptorResolver;
    private final DelegatedPropertyResolver delegatedPropertyResolver;
    private final TypeResolver typeResolver;
    private final QualifiedExpressionResolver qualifiedExpressionResolver;
    private final CandidateResolver candidateResolver;
    private final JetImportsFactory jetImportsFactory;
    private final ScopeProvider scopeProvider;
    private final LockBasedStorageManager lockBasedStorageManager;
    
    public InjectorForLazyResolve(
        @NotNull Project project,
        @NotNull FileBaseDeclarationConfiguration fileBaseDeclarationConfiguration,
        @NotNull ModuleDescriptorImpl moduleDescriptor,
        @NotNull BindingTrace bindingTrace
    ) {
        this.project = project;
        this.fileBaseDeclarationConfiguration = fileBaseDeclarationConfiguration;
        this.moduleDescriptor = moduleDescriptor;
        this.bindingTrace = bindingTrace;
        this.lockBasedStorageManager = new LockBasedStorageManager();
        this.lockBasedLazyResolveStorageManager = new LockBasedLazyResolveStorageManager(lockBasedStorageManager);
        this.fileBasedDeclarationProviderFactory = new FileBasedDeclarationProviderFactory(lockBasedLazyResolveStorageManager, fileBaseDeclarationConfiguration);
        this.resolveSession = new ResolveSession(lockBasedLazyResolveStorageManager, moduleDescriptor, fileBasedDeclarationProviderFactory, bindingTrace);
        this.callResolverExtensionProvider = new CallResolverExtensionProvider();
        this.platformToKotlinClassMap = moduleDescriptor.getPlatformToKotlinClassMap();
        this.annotationResolver = new AnnotationResolver();
        this.callResolver = new CallResolver();
        this.argumentTypeResolver = new ArgumentTypeResolver();
        this.expressionTypingServices = new ExpressionTypingServices();
        this.callExpressionResolver = new CallExpressionResolver();
        this.descriptorResolver = new DescriptorResolver();
        this.delegatedPropertyResolver = new DelegatedPropertyResolver();
        this.typeResolver = new TypeResolver();
        this.qualifiedExpressionResolver = new QualifiedExpressionResolver();
        this.candidateResolver = new CandidateResolver();
        this.jetImportsFactory = new JetImportsFactory();
        this.scopeProvider = new ScopeProvider(getResolveSession());

        this.resolveSession.setAnnotationResolve(annotationResolver);
        this.resolveSession.setDescriptorResolver(descriptorResolver);
        this.resolveSession.setJetImportFactory(jetImportsFactory);
        this.resolveSession.setQualifiedExpressionResolver(qualifiedExpressionResolver);
        this.resolveSession.setScopeProvider(scopeProvider);
        this.resolveSession.setTypeResolver(typeResolver);

        annotationResolver.setCallResolver(callResolver);
        annotationResolver.setExpressionTypingServices(expressionTypingServices);

        callResolver.setArgumentTypeResolver(argumentTypeResolver);
        callResolver.setCandidateResolver(candidateResolver);
        callResolver.setExpressionTypingServices(expressionTypingServices);
        callResolver.setTypeResolver(typeResolver);

        argumentTypeResolver.setExpressionTypingServices(expressionTypingServices);
        argumentTypeResolver.setTypeResolver(typeResolver);

        expressionTypingServices.setAnnotationResolver(annotationResolver);
        expressionTypingServices.setCallExpressionResolver(callExpressionResolver);
        expressionTypingServices.setCallResolver(callResolver);
        expressionTypingServices.setDescriptorResolver(descriptorResolver);
        expressionTypingServices.setExtensionProvider(callResolverExtensionProvider);
        expressionTypingServices.setPlatformToKotlinClassMap(platformToKotlinClassMap);
        expressionTypingServices.setProject(project);
        expressionTypingServices.setTypeResolver(typeResolver);

        callExpressionResolver.setExpressionTypingServices(expressionTypingServices);

        descriptorResolver.setAnnotationResolver(annotationResolver);
        descriptorResolver.setDelegatedPropertyResolver(delegatedPropertyResolver);
        descriptorResolver.setExpressionTypingServices(expressionTypingServices);
        descriptorResolver.setTypeResolver(typeResolver);

        delegatedPropertyResolver.setExpressionTypingServices(expressionTypingServices);

        typeResolver.setAnnotationResolver(annotationResolver);
        typeResolver.setModuleDescriptor(moduleDescriptor);
        typeResolver.setQualifiedExpressionResolver(qualifiedExpressionResolver);

        candidateResolver.setArgumentTypeResolver(argumentTypeResolver);

        jetImportsFactory.setProject(project);

    }
    
    @PreDestroy
    public void destroy() {
    }
    
    public ResolveSession getResolveSession() {
        return this.resolveSession;
    }
    
}
