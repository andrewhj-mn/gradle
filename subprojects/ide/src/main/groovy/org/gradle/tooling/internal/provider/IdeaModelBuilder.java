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

package org.gradle.tooling.internal.provider;

import org.gradle.api.Project;
import org.gradle.api.internal.GradleInternal;
import org.gradle.plugins.ide.idea.IdeaPlugin;
import org.gradle.plugins.ide.idea.model.*;
import org.gradle.tooling.internal.idea.DefaultIdeaLibraryDependency;
import org.gradle.tooling.internal.idea.DefaultIdeaModule;
import org.gradle.tooling.internal.idea.DefaultIdeaModuleDependency;
import org.gradle.tooling.internal.idea.DefaultIdeaProject;
import org.gradle.tooling.internal.protocol.ProjectVersion3;
import org.gradle.tooling.model.idea.IdeaDependency;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author: Szczepan Faber, created at: 7/19/11
 */
public class IdeaModelBuilder implements BuildsModel {

    private boolean projectDependenciesOnly = true;
    private final TasksFactory tasksFactory;

    public IdeaModelBuilder(boolean includeTasks, boolean projectDependenciesOnly) {
        this.tasksFactory = new TasksFactory(includeTasks);
        this.projectDependenciesOnly = projectDependenciesOnly;
    }

    public Object buildAll(GradleInternal gradle) {
        Project root = gradle.getRootProject();
        tasksFactory.collectTasks(root);
        new IdeaPluginApplier().apply(root);
        ProjectVersion3 ideaProject = buildHierarchy(root);
        return ideaProject;
    }

    private ProjectVersion3 buildHierarchy(Project project) {
        IdeaModel ideaModel = project.getPlugins().getPlugin(IdeaPlugin.class).getModel();
        IdeaProject projectModel = ideaModel.getProject();

        DefaultIdeaProject newProject = new DefaultIdeaProject(projectModel.getName(), project.getPath(), null, project.getProjectDir());
        newProject.setJavaVersion(projectModel.getJavaVersion().toString());
        newProject.setLanguageLevel(projectModel.getLanguageLevel().getFormatted());

        List<DefaultIdeaModule> modules = new LinkedList<DefaultIdeaModule>();
        for (IdeaModule module: projectModel.getModules()) {
            buildModule(modules, module, newProject);
        }
        newProject.setModules(modules);

        return newProject;
    }

    private void buildModule(List<DefaultIdeaModule> modules, IdeaModule module, DefaultIdeaProject newProject) {
        DefaultIdeaModule defaultIdeaModule = new DefaultIdeaModule();
        defaultIdeaModule.setSourceDirectories(new LinkedList<File>(module.getSourceDirs()));
        defaultIdeaModule.setTestDirectories(new LinkedList<File>(module.getTestSourceDirs()));
        defaultIdeaModule.setExcludeDirectories(new LinkedList<File>(module.getExcludeDirs()));
        defaultIdeaModule.setContentRoot(module.getContentRoot());
        defaultIdeaModule.setName(module.getName());
        defaultIdeaModule.setProject(newProject);
        defaultIdeaModule.setModuleFileDir(module.getIml().getGenerateTo());
        defaultIdeaModule.setInheritOutputDirs(module.getInheritOutputDirs() != null ? module.getInheritOutputDirs() : false);
        defaultIdeaModule.setOutputDir(module.getOutputDir());
        defaultIdeaModule.setTestOutputDir(module.getTestOutputDir());

        List<IdeaDependency> dependencies = new LinkedList<IdeaDependency>();
        Set<Dependency> resolved = module.resolveDependencies();
        for (Dependency dependency: resolved) {
            if (dependency instanceof ModuleLibrary) {
                File file = ((ModuleLibrary) dependency).getSingleJar();
                File javadoc = ((ModuleLibrary) dependency).getSingleJavadoc();
                File source = ((ModuleLibrary) dependency).getSingleSource();
                boolean exported = ((ModuleLibrary) dependency).getExported();
                IdeaDependency.Scope scope = IdeaDependency.Scope.valueOf(((ModuleLibrary) dependency).getScope());
                dependencies.add(new DefaultIdeaLibraryDependency(file, source, javadoc, scope, exported));
            } else if (dependency instanceof ModuleDependency) {
                String name = ((ModuleDependency) dependency).getName();
                boolean exported = ((ModuleDependency) dependency).getExported();
                IdeaDependency.Scope scope = IdeaDependency.Scope.valueOf(((ModuleDependency) dependency).getScope());
                dependencies.add(new DefaultIdeaModuleDependency(scope, name, exported));
            }
        }
        defaultIdeaModule.setDependencies(dependencies);

        modules.add(defaultIdeaModule);
    }
}
