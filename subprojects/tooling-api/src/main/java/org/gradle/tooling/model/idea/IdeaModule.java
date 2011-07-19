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

import java.io.File;
import java.util.List;

/**
 * Model for idea module information
 *
 * @author: Szczepan Faber, created at: 7/19/11
 */
public interface IdeaModule {

    /**
     * @return the module name; not-<code>null</code>
     */
    String getName();

    /**
     * @return the folder containing module file (*.iml)
     */
    File getModuleFileDir();

    /**
     * @return not-<code>null</code> instance of the project current module belongs to
     */
    IdeaProject getProject();

    /**
     * @return content root
     */
    File getContentRoot();

    /**
     * @return source dirs
     */
    List<File> getSourceDirectories();

    /**
     * @return test dirs
     */
    List<File> getTestDirectories();

    /**
     * @return test dirs
     */
    List<File> getExcludeDirectories();

    /**
     * @return whether current module should inherit project's output directory.
     * @see #getOutputDir()
     * @see #getTestOutputDir()
     */
    boolean getInheritOutputDirs();

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
     * @return ordered collection of dependencies. Dependencies order affects module classpath
     */
    List<? extends IdeaDependency> getDependencies();
}
