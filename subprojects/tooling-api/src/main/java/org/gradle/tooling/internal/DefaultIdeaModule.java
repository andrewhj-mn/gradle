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

package org.gradle.tooling.internal;

import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;

import java.io.File;
import java.util.List;

/**
 * @author: Szczepan Faber, created at: 7/19/11
 */
public class DefaultIdeaModule implements IdeaModule {

    String name;
    List<File> sourceDirectories;
    List<File> testDirectories;
    List<File> excludeDirectories;
    File contentRoot;
    IdeaProject ideaProject;
    File moduleFileDir;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<File> getSourceDirectories() {
        return sourceDirectories;
    }

    public void setSourceDirectories(List<File> sourceDirectories) {
        this.sourceDirectories = sourceDirectories;
    }

    public List<File> getTestDirectories() {
        return testDirectories;
    }

    public void setTestDirectories(List<File> testDirectories) {
        this.testDirectories = testDirectories;
    }

    public List<File> getExcludeDirectories() {
        return excludeDirectories;
    }

    public void setExcludeDirectories(List<File> excludeDirectories) {
        this.excludeDirectories = excludeDirectories;
    }

    public File getContentRoot() {
        return contentRoot;
    }

    public void setContentRoot(File contentRoot) {
        this.contentRoot = contentRoot;
    }

    public IdeaProject getProject() {
        return ideaProject;
    }

    public void setProject(IdeaProject ideaProject) {
        this.ideaProject = ideaProject;
    }

    public File getModuleFileDir() {
        return moduleFileDir;
    }

    public void setModuleFileDir(File moduleFileDir) {
        this.moduleFileDir = moduleFileDir;
    }
}