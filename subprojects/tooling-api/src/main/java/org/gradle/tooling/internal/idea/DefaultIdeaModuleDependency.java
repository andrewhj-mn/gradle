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

import org.gradle.tooling.model.idea.IdeaModuleDependency;

import java.io.Serializable;

/**
 * @author: Szczepan Faber, created at: 7/19/11
 */
public class DefaultIdeaModuleDependency implements IdeaModuleDependency, Serializable {

    private static final long serialVersionUID = 1L;

    private String scope;
    private String dependencyModuleName;
    private boolean exported;

    public DefaultIdeaModuleDependency(String scope, String dependencyModuleName, boolean exported) {
        this.scope = scope;
        this.dependencyModuleName = dependencyModuleName;
        this.exported = exported;
    }

    public String getScope() {
        return scope;
    }

    public String getDependencyModuleName() {
        return dependencyModuleName;
    }

    public Boolean getExported() {
        return exported;
    }

    @Override
    public String toString() {
        return "DefaultIdeaModuleDependency{" +
                "scope=" + scope +
                ", dependencyModuleName='" + dependencyModuleName + '\'' +
                ", exported=" + exported +
                '}';
    }
}
