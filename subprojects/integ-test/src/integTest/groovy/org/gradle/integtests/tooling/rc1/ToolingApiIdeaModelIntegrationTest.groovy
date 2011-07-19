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
package org.gradle.integtests.tooling.rc1

import org.gradle.integtests.tooling.fixture.ToolingApiSpecification
import org.gradle.tooling.model.idea.IdeaProject

class ToolingApiIdeaModelIntegrationTest extends ToolingApiSpecification {

    def "builds the model even if idea plugin not applied"() {
        def projectDir = dist.testDir
        projectDir.file('build.gradle').text = '''
apply plugin: 'java'
description = 'this is a project'
'''
        projectDir.file('settings.gradle').text = 'rootProject.name = \"test project\"'

        when:
        IdeaProject project = withConnection { connection -> connection.getModel(IdeaProject.class) }

        then:
        project.name == 'test project'
        project.description == null
        project.projectDirectory == projectDir
    }

    def "provides basic project information"() {
        def projectDir = dist.testDir
        projectDir.file('build.gradle').text = """
apply plugin: 'java'
apply plugin: 'idea'

idea.project {
  languageLevel = '1.5'
  javaVersion = '1.6'
}
"""

        when:
        IdeaProject project = withConnection { connection -> connection.getModel(IdeaProject.class) }

        then:
        project.languageLevel == 'JDK_1_5'
        project.javaVersion == '1.6'
    }
}
