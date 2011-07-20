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

import org.gradle.tooling.model.DomainObjectSet;
import org.gradle.tooling.model.idea.IdeaLibraryDependency;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaModuleDependency;
import org.gradle.tooling.model.idea.IdeaProject;
import org.gradle.tooling.model.internal.ImmutableDomainObjectSet;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: Szczepan Faber, created at: 7/19/11
 */
public class DefaultIdeaModule implements IdeaModule, Serializable {

    private static final long serialVersionUID = 1L;

    String name;
    List<File> sourceDirectories;
    List<File> testDirectories;
    List<File> excludeDirectories;
    File contentRoot;
    IdeaProject project;
    File moduleFileDir;
    boolean inheritOutputDirs;
    File outputDir;
    File testOutputDir;
    List<? extends IdeaLibraryDependency> libraryDependencies = new LinkedList<IdeaLibraryDependency>();
    List<? extends IdeaModuleDependency> moduleDependencies = new LinkedList<IdeaModuleDependency>();

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
        return project;
    }

    public void setProject(IdeaProject project) {
        this.project = project;
    }

    public File getModuleFileDir() {
        return moduleFileDir;
    }

    public void setModuleFileDir(File moduleFileDir) {
        this.moduleFileDir = moduleFileDir;
    }

    public Boolean getInheritOutputDirs() {
        return inheritOutputDirs;
    }

    public void setInheritOutputDirs(boolean inheritOutputDirs) {
        this.inheritOutputDirs = inheritOutputDirs;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public File getTestOutputDir() {
        return testOutputDir;
    }

    public void setTestOutputDir(File testOutputDir) {
        this.testOutputDir = testOutputDir;
    }

    public DomainObjectSet<? extends IdeaLibraryDependency> getLibraryDependencies() {
        return new ImmutableDomainObjectSet<IdeaLibraryDependency>(libraryDependencies);
    }

    public DomainObjectSet<? extends IdeaModuleDependency> getModuleDependencies() {
        return new ImmutableDomainObjectSet<IdeaModuleDependency>(moduleDependencies);
    }

    public void setLibraryDependencies(List<? extends IdeaLibraryDependency> libraryDependencies) {
        this.libraryDependencies = libraryDependencies;
    }

    public void setModuleDependencies(List<? extends IdeaModuleDependency> moduleDependencies) {
        this.moduleDependencies = moduleDependencies;
    }
}