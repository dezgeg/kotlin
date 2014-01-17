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

package org.jetbrains.jet.lang.resolve.constants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.annotations.AnnotationArgumentVisitor;
import org.jetbrains.jet.lang.types.JetType;
import org.jetbrains.jet.lang.types.lang.KotlinBuiltIns;

public abstract class CompileTimeConstant<T> {
    protected final T value;
    private boolean canBeUsedInAnnotations = true;

    protected CompileTimeConstant(T value) {
        this.value = value;
    }

    public boolean canBeUsedInAnnotations() {
        return canBeUsedInAnnotations;
    }

    public void setCanBeUsedInAnnotations(boolean canBeUsedInAnnotations) {
        this.canBeUsedInAnnotations = canBeUsedInAnnotations;
    }

    @Nullable
    public T getValue() {
        return value;
    }

    @NotNull
    public abstract JetType getType(@NotNull KotlinBuiltIns kotlinBuiltIns);

    public abstract <R, D> R accept(AnnotationArgumentVisitor<R, D> visitor, D data);
}
