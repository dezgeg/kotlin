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

package org.jetbrains.jet.plugin.vfilefinder;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.resolve.kotlin.VirtualFileFinder;
import org.jetbrains.jet.lang.resolve.kotlin.VirtualFileKotlinClassFinder;
import org.jetbrains.jet.lang.resolve.name.FqName;

import java.util.Collection;

public final class IDEVirtualFileFinder extends VirtualFileKotlinClassFinder implements VirtualFileFinder {

    private static final Logger LOG = Logger.getInstance(IDEVirtualFileFinder.class);

    @NotNull private final Project project;

    public IDEVirtualFileFinder(@NotNull Project project) {
        this.project = project;
    }

    @Nullable
    @Override
    public VirtualFile findVirtualFile(@NotNull FqName className) {
        Collection<VirtualFile> files =
                FileBasedIndex.getInstance().getContainingFiles(KotlinClassFileIndex.KEY, className, GlobalSearchScope.allScope(project));
        if (files.isEmpty()) {
            return null;
        }
        if (files.size() > 1) {
            LOG.warn("There are " + files.size() + " classes with same fqName: " + className + " found.");
        }
        return files.iterator().next();
    }
}
