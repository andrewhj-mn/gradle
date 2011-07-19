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
import org.gradle.plugins.ide.idea.model.IdeaModel;
import org.gradle.plugins.ide.idea.model.IdeaModule;
import org.gradle.plugins.ide.idea.model.IdeaProject;
import org.gradle.tooling.internal.DefaultIdeaModule;
import org.gradle.tooling.internal.DefaultIdeaProject;
import org.gradle.tooling.internal.protocol.ProjectVersion3;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: Szczepan Faber, created at: 7/19/11
 */
public class IdeaModelBuilder implements BuildsModel {

    private boolean projectDependenciesOnly = true;
//    private final Map<String, ProjectVersion3> projectMapping = new HashMap<String, ProjectVersion3>();
    private GradleInternal gradle;
    private final TasksFactory tasksFactory;

    public IdeaModelBuilder(boolean includeTasks, boolean projectDependenciesOnly) {
        this.tasksFactory = new TasksFactory(includeTasks);
        this.projectDependenciesOnly = projectDependenciesOnly;
    }

    public Object buildAll(GradleInternal gradle) {
        this.gradle = gradle;
        Project root = gradle.getRootProject();
        tasksFactory.collectTasks(root);
        new IdeaPluginApplier().apply(root);
        ProjectVersion3 ideaProject = buildHierarchy(root);
        populate(root);
        return ideaProject;
    }

//    private void addProject(Project project, ProjectVersion3 ideProject) {
//        if (project == gradle.getDefaultProject()) {
//            currentProject = ideProject;
//        }
//        projectMapping.put(project.getPath(), ideProject);
//    }

    private void populate(Project project) {
        IdeaModel ideaModel = project.getPlugins().getPlugin(IdeaPlugin.class).getModel();
//        EclipseClasspath classpath = eclipseModel.getClasspath();
//
//        classpath.setProjectDependenciesOnly(projectDependenciesOnly);
//        List<ClasspathEntry> entries = classpath.resolveDependencies();
//
//        final List<ExternalDependencyVersion1> externalDependencies = new LinkedList<ExternalDependencyVersion1>();
//        final List<EclipseProjectDependencyVersion2> projectDependencies = new LinkedList<EclipseProjectDependencyVersion2>();
//        final List<EclipseSourceDirectoryVersion1> sourceDirectories = new LinkedList<EclipseSourceDirectoryVersion1>();
//
//        for (ClasspathEntry entry : entries) {
//            if (entry instanceof Library) {
//                Library library = (Library) entry;
//                final File file = project.file(library.getPath());
//                final File source = library.getSourcePath() == null ? null : project.file(library.getSourcePath());
//                final File javadoc = library.getJavadocPath() == null ? null : project.file(library.getJavadocPath());
//                externalDependencies.add(new DefaultExternalDependency(file, javadoc, source));
//            } else if (entry instanceof ProjectDependency) {
//                final ProjectDependency projectDependency = (ProjectDependency) entry;
//                final String path = StringUtils.removeStart(projectDependency.getPath(), "/");
//                projectDependencies.add(new DefaultEclipseProjectDependency(path, projectMapping.get(projectDependency.getGradlePath())));
//            } else if (entry instanceof SourceFolder) {
//                String path = ((SourceFolder) entry).getPath();
//                sourceDirectories.add(new DefaultEclipseSourceDirectory(path, project.file(path)));
//            }
//        }
//
//        final EclipseProjectVersion3 eclipseProject = projectMapping.get(project.getPath());
//        ReflectionUtil.setProperty(eclipseProject, "classpath", externalDependencies);
//        ReflectionUtil.setProperty(eclipseProject, "projectDependencies", projectDependencies);
//        ReflectionUtil.setProperty(eclipseProject, "sourceDirectories", sourceDirectories);
//
//        if (ReflectionUtil.hasProperty(eclipseProject, "linkedResources")) {
//            List<DefaultEclipseLinkedResource> linkedResources = new LinkedList<DefaultEclipseLinkedResource>();
//            for(Link r: eclipseModel.getProject().getLinkedResources()) {
//                linkedResources.add(new DefaultEclipseLinkedResource(r.getName(), r.getType(), r.getLocation(), r.getLocationUri()));
//            }
//            ReflectionUtil.setProperty(eclipseProject, "linkedResources", linkedResources);
//        }
//
//        List<EclipseTaskVersion1> out = new ArrayList<EclipseTaskVersion1>();
//        for (final Task t : tasksFactory.getTasks(project)) {
//            out.add(new DefaultEclipseTask(eclipseProject, t.getPath(), t.getName(), t.getDescription()));
//        }
//        ReflectionUtil.setProperty(eclipseProject, "tasks", out);
//
//        for (Project childProject : project.getChildProjects().values()) {
//            populate(childProject);
//        }
    }

    private ProjectVersion3 buildHierarchy(Project project) {
//        List<ProjectVersion3> children = new ArrayList<ProjectVersion3>();
//        for (Project child : project.getChildProjects().values()) {
//            children.add(buildHierarchy(child));
//        }

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

//        addProject(project, newProject);
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
        modules.add(defaultIdeaModule);
    }
}
