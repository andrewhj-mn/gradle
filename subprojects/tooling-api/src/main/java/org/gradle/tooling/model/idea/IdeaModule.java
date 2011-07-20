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

package org.gradle.tooling.model.idea;

import org.gradle.tooling.model.DomainObjectSet;

import java.io.File;
import java.util.List;

/**
 * Model for idea module information
 *
 * @author: Szczepan Faber, created at: 7/19/11
 */
public interface IdeaModule {

    /**
     * the module name; not-<code>null</code>
     *
     * @return module name
     */
    String getName();

    /**
     * the folder containing module file (*.iml)
     *
     * @return module file dir
     */
    File getModuleFileDir();

    /**
     * not-<code>null</code> instance of the project current module belongs to
     *
     * @return project
     */
    IdeaProject getProject();

    /**
     * content root
     *
     * @return content root
     */
    File getContentRoot();

    /**
     * source dirs
     *
     * @return source dirs
     */
    List<File> getSourceDirectories();

    /**
     * test dirs
     *
     * @return test dirs
     */
    List<File> getTestDirectories();

    /**
     * test dirs
     *
     * @return test dirs
     */
    List<File> getExcludeDirectories();

    /**
     * whether current module should inherit project's output directory.
     *
     * @return inherit output dirs flag
     * @see #getOutputDir()
     * @see #getTestOutputDir()
     */
    Boolean getInheritOutputDirs();

    /**
     * directory to store module's production classes and resources.
     *
     * @return directory to store production output. non-<code>null</code> if
     *            {@link #getInheritOutputDirs()} returns <code>'false'</code>
     */
    File getOutputDir();

    /**
     * directory to store module's test classes and resources.
     *
     * @return directory to store test output. Is expected to be non-<code>null</code> if
     *            {@link #getInheritOutputDirs()} returns <code>'false'</code>
     */
    File getTestOutputDir();

    /**
     * ordered collection of module dependencies
     *
     * @return dependencies
     */
    DomainObjectSet<? extends IdeaModuleDependency> getModuleDependencies();

    /**
     * ordered collection of library dependencies
     *
     * @return dependencies
     */
    DomainObjectSet<? extends IdeaLibraryDependency> getLibraryDependencies();
}
