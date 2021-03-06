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

package org.jetbrains.jet.lang.resolve.lazy;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.CallableDescriptor;
import org.jetbrains.jet.lang.descriptors.DeclarationDescriptor;
import org.jetbrains.jet.lang.descriptors.annotations.Annotations;
import org.jetbrains.jet.lang.resolve.scopes.JetScope;
import org.jetbrains.jet.lang.types.JetType;
import org.jetbrains.jet.lang.types.TypeConstructor;
import org.jetbrains.jet.lang.types.TypeProjection;

import java.util.Collection;

public class ForceResolveUtil {
    private static final Logger LOG = Logger.getInstance(ForceResolveUtil.class);

    private ForceResolveUtil() {}

    public static void forceResolveAllContents(@NotNull DeclarationDescriptor descriptor) {
        LOG.debug("descriptor: " + descriptor);
        doForceResolveAllContents(descriptor);
        LOG.debug("<<< " + descriptor);
    }

    public static void forceResolveAllContents(@NotNull JetScope scope) {
        forceResolveAllContents(scope.getAllDescriptors());
    }

    public static void forceResolveAllContents(@NotNull Iterable<? extends DeclarationDescriptor> descriptors) {
        for (DeclarationDescriptor descriptor : descriptors) {
            forceResolveAllContents(descriptor);
        }
    }

    public static void forceResolveAllContents(@NotNull Collection<JetType> types) {
        for (JetType type : types) {
            forceResolveAllContents(type);
        }
    }

    public static void forceResolveAllContents(@NotNull TypeConstructor typeConstructor) {
        LOG.debug("descriptor: " + typeConstructor);
        doForceResolveAllContents(typeConstructor);
        LOG.debug("<<< " + typeConstructor);
    }

    public static void forceResolveAllContents(@NotNull Annotations annotations) {
        doForceResolveAllContents(annotations);
    }

    private static void doForceResolveAllContents(Object object) {
        if (object instanceof LazyEntity) {
            LazyEntity lazyEntity = (LazyEntity) object;
            lazyEntity.forceResolveAllContents();
        }
        else if (object instanceof CallableDescriptor) {
            CallableDescriptor callableDescriptor = (CallableDescriptor) object;
            forceResolveAllContents(callableDescriptor.getReturnType());
        }
    }

    public static void forceResolveAllContents(@Nullable JetType type) {
        if (type == null) return;

        forceResolveAllContents(type.getConstructor());
        for (TypeProjection projection : type.getArguments()) {
            forceResolveAllContents(projection.getType());
        }
    }
}
