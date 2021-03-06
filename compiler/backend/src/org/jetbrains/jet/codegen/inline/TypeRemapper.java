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

package org.jetbrains.jet.codegen.inline;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.asm4.commons.Remapper;

import java.util.Map;

public class TypeRemapper extends Remapper {

    @NotNull
    private final Map<String, String> typeMapping;

    //typeMapping could be changed outside through method processing
    public TypeRemapper(@NotNull Map<String, String> typeMapping) {
        this.typeMapping = typeMapping;
    }

    @Override
    public String map(String type) {
        String newType = typeMapping.get(type);
        if (newType != null) {
            return newType;
        }

        return type;
    }
}
