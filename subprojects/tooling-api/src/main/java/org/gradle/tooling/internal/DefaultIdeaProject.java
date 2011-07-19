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

import org.gradle.tooling.internal.protocol.ProjectVersion3;
import org.gradle.tooling.model.idea.IdeaProject;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class DefaultIdeaProject implements IdeaProject, Serializable, ProjectVersion3 {

    private String path;
    private String name;
    private String description;
    private File projectDirectory;
    private List<ProjectVersion3> children;
    private ProjectVersion3 parent;
    private String javaVersion;
    private String languageLevel;

    public DefaultIdeaProject(String name, String path, String description, File projectDirectory, List<ProjectVersion3> children) {
        this.path = path;
        this.name = name;
        this.description = description;
        this.projectDirectory = projectDirectory;
        this.children = children;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public File getProjectDirectory() {
        return projectDirectory;
    }

    public ProjectVersion3 getParent() {
        return parent;
    }

    public void setParent(ProjectVersion3 parent) {
        this.parent = parent;
    }

    public List<ProjectVersion3> getChildren() {
        return children;
    }

    public void setChildren(List<ProjectVersion3> children) {
        this.children = children;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getLanguageLevel() {
        return languageLevel;
    }

    public void setLanguageLevel(String languageLevel) {
        this.languageLevel = languageLevel;
    }
}