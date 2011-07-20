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
        project.modules.size() == 1
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

    def "provides all modules"() {
        def projectDir = dist.testDir
        projectDir.file('build.gradle').text = '''
subprojects {
    apply plugin: 'java'
}
'''
        projectDir.file('settings.gradle').text = "include 'api', 'impl'"

        when:
        IdeaProject project = withConnection { connection -> connection.getModel(IdeaProject.class) }

        then:
        project.modules.size() == 3
        project.modules.any { it.name == 'api' }
        project.modules.any { it.name == 'impl' }
    }

    def "provides dependencies"() {
        def projectDir = dist.testDir
        projectDir.file('build.gradle').text = '''
subprojects {
    apply plugin: 'java'
}

project(':impl') {
    repositories { mavenCentral() }

    dependencies {
        compile project(':api')
        testCompile 'junit:junit:4.5'
    }
}
'''
        projectDir.file('settings.gradle').text = "include 'api', 'impl'"

        when:
        IdeaProject project = withConnection { connection -> connection.getModel(IdeaProject.class) }
        def module = project.modules.find { it.name == 'impl' }

        then:
        def deps = module.dependencies.all.sort { a,b ->
            a.hasProperty("javadoc") ? 0 : 1
        }
        deps.size() == 2

        deps[0].file.exists()
        deps[0].file.path.endsWith('junit-4.5.jar')
        //TODO SF find library that has javadocs :)
//        deps[0].javadoc.exists()
        deps[0].source.exists()
        deps[0].source.path.endsWith('junit-4.5-sources.jar')
        deps[0].scope.toString() == 'TEST'

        deps[1].dependencyModuleName == 'api'
        deps[1].scope.toString() == 'COMPILE'
        deps[1].exported
    }

    def "provides basic module information"() {
        def projectDir = dist.testDir
        projectDir.file('build.gradle').text = """
apply plugin: 'java'
apply plugin: 'idea'

idea.module.inheritOutputDirs = false
idea.module.outputDir = file('someDir')
idea.module.testOutputDir = file('someTestDir')
"""

        when:
        IdeaProject project = withConnection { connection -> connection.getModel(IdeaProject.class) }
        def module = project.modules[0]

        then:
        module.contentRoot == projectDir
        module.project.projectDirectory == project.projectDirectory
        module.moduleFileDir == dist.testDir
        !module.inheritOutputDirs
        module.outputDir == projectDir.file('someDir')
        module.testOutputDir == projectDir.file('someTestDir')
    }

    def "provides source dir information"() {
        def projectDir = dist.testDir
        projectDir.file('build.gradle').text = "apply plugin: 'java'"

        projectDir.create {
            src {
                main {
                    java {}
                    resources {}
                }
                test {
                    java {}
                    resources {}
                }
            }
        }

        when:
        IdeaProject project = withConnection { connection -> connection.getModel(IdeaProject.class) }
        def module = project.modules[0]

        then:
        module.sourceDirectories.size() == 2
        module.sourceDirectories.any { it.path.endsWith 'src/main/java' }
        module.sourceDirectories.any { it.path.endsWith 'src/main/resources' }

        module.testDirectories.size() == 2
        module.testDirectories.any { it.path.endsWith 'src/test/java' }
        module.testDirectories.any { it.path.endsWith 'src/test/resources' }
    }

    def "provides excluded dir information"() {
        def projectDir = dist.testDir
        projectDir.file('build.gradle').text = """
apply plugin: 'java'
apply plugin: 'idea'

idea.module.excludeDirs += file('foo')
"""

        when:
        IdeaProject project = withConnection { connection -> connection.getModel(IdeaProject.class) }
        def module = project.modules[0]

        then:
        module.excludeDirectories.any { it.path.endsWith 'foo' }
    }
}
