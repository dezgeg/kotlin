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

package org.jetbrains.jet.j2k.ast


public class WhiteSpace(val text: String) : Element {
    override fun toKotlin() = text
    override fun isEmpty() = text.isEmpty()

    public fun compareTo(other: WhiteSpace): Int {

        fun newLinesCount(w: WhiteSpace) = w.text.count { it == '\n' }

        val lineCountDiff = newLinesCount(this) - newLinesCount(other)
        if (lineCountDiff != 0) {
            return lineCountDiff
        }

        fun spacesCount(w: WhiteSpace) = w.text.count { it == ' ' }

        return spacesCount(this) - spacesCount(other)
    }

    class object {
        public val NewLine: WhiteSpace = WhiteSpace("\n")
    }
}

