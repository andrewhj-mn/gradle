/*
 * Copyright 2010 the original author or authors.
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
import org.gradle.tooling.model.Project;

/**
 * First hacky approach to provide idea information
 */
public interface IdeaProject extends Project {

  /**
   * Language level to use within the current project.
   *
   * @return language level
   */
  String getLanguageLevel();

  /**
   * modules of the current project.
   *
   * @return modules
   */
  DomainObjectSet<? extends IdeaModule> getModules();

  /**
   * not-<code>null</code> version of the JDK to use with the current project
   *
   * @return jdk name
   */
  String getJavaVersion();

}
