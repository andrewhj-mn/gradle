/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.tooling.internal.idea;

import org.gradle.tooling.model.idea.IdeaLibraryDependency;

import java.io.File;
import java.io.Serializable;

/**
 * @author: Szczepan Faber, created at: 7/19/11
 */
public class DefaultIdeaLibraryDependency implements IdeaLibraryDependency, Serializable {

    private static final long serialVersionUID = 1L;

    private File file;
    private File source;
    private File javadoc;
    private Scope scope;
    private boolean exported;

    public DefaultIdeaLibraryDependency(File file, File source, File javadoc, Scope scope, boolean exported) {
        this.file = file;
        this.source = source;
        this.javadoc = javadoc;
        this.scope = scope;
        this.exported = exported;
    }

    public File getFile() {
        return file;
    }

    public File getSource() {
        return source;
    }

    public File getJavadoc() {
        return javadoc;
    }

    public Scope getScope() {
        return scope;
    }

    public boolean getExported() {
        return exported;
    }
}