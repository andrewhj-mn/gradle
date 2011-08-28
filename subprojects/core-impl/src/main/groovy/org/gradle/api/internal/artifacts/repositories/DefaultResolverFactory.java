/*
 * Copyright 2007 the original author or authors.
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

package org.gradle.api.internal.artifacts.repositories;

import org.apache.ivy.plugins.resolver.*;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.ResolverContainer;
import org.gradle.api.artifacts.dsl.IvyArtifactRepository;
import org.gradle.api.artifacts.dsl.MavenArtifactRepository;
import org.gradle.api.artifacts.maven.*;
import org.gradle.api.internal.Factory;
import org.gradle.api.internal.artifacts.ResolverFactory;
import org.gradle.api.internal.artifacts.ivyservice.LocalFileRepositoryCacheManager;
import org.gradle.api.internal.artifacts.publish.maven.*;
import org.gradle.api.internal.artifacts.publish.maven.deploy.BaseMavenInstaller;
import org.gradle.api.internal.artifacts.publish.maven.deploy.BasePomFilterContainer;
import org.gradle.api.internal.artifacts.publish.maven.deploy.DefaultArtifactPomContainer;
import org.gradle.api.internal.artifacts.publish.maven.deploy.groovy.DefaultGroovyMavenDeployer;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.logging.LoggingManagerInternal;
import org.jfrog.wharf.ivy.resolver.UrlWharfResolver;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Hans Dockter
 */
public class DefaultResolverFactory implements ResolverFactory {
    private final Factory<LoggingManagerInternal> loggingManagerFactory;
    private final MavenFactory mavenFactory;
    private final LocalMavenCacheLocator localMavenCacheLocator;
    private final FileResolver fileResolver;

    public DefaultResolverFactory(Factory<LoggingManagerInternal> loggingManagerFactory, MavenFactory mavenFactory, LocalMavenCacheLocator localMavenCacheLocator, FileResolver fileResolver) {
        this.loggingManagerFactory = loggingManagerFactory;
        this.mavenFactory = mavenFactory;
        this.localMavenCacheLocator = localMavenCacheLocator;
        this.fileResolver = fileResolver;
    }

    public DependencyResolver createResolver(Object userDescription) {
        DependencyResolver result;
        if (userDescription instanceof String) {
            result = createMavenRepoResolver((String) userDescription, userDescription);
        } else if (userDescription instanceof Map) {
            Map<String, ?> userDescriptionMap = (Map<String, ?>) userDescription;
            result = createMavenRepoResolver(userDescriptionMap.get(ResolverContainer.RESOLVER_NAME).toString(),
                    userDescriptionMap.get(ResolverContainer.RESOLVER_URL));
        } else if (userDescription instanceof DependencyResolver) {
            result = (DependencyResolver) userDescription;
        } else {
            throw new InvalidUserDataException("Illegal Resolver type");
        }
        return result;
    }

    public FileSystemResolver createFlatDirResolver(String name, Object... roots) {
        FileSystemResolver resolver = new FileSystemResolver();
        resolver.setName(name);
        for (Object root : roots) {
            File rootFile = fileResolver.resolve(root);
            resolver.addArtifactPattern(rootFile.getAbsolutePath() + "/[artifact]-[revision](-[classifier]).[ext]");
            resolver.addArtifactPattern(rootFile.getAbsolutePath() + "/[artifact](-[classifier]).[ext]");
        }
        resolver.setValidate(false);
        resolver.setRepositoryCacheManager(new LocalFileRepositoryCacheManager(name));
        return resolver;
    }

    public AbstractResolver createMavenLocalResolver(String name) {
        File cacheDir = localMavenCacheLocator.getLocalMavenCache();
        return createMavenRepoResolver(name, cacheDir);
    }

    public AbstractResolver createMavenRepoResolver(String name, Object root, Object... jarRepoUrls) {
        URI rootUri = fileResolver.resolveUri(root);
        BasicResolver iBiblioResolver = createIBiblioResolver(name, rootUri);
        if (jarRepoUrls.length == 0) {
            iBiblioResolver.setDescriptor(IBiblioResolver.DESCRIPTOR_OPTIONAL);
            return iBiblioResolver;
        }
        iBiblioResolver.setName(iBiblioResolver.getName() + "_poms");
        DependencyResolver urlResolver = createUrlResolver(name, rootUri, jarRepoUrls);
        return createDualResolver(name, iBiblioResolver, urlResolver);
    }

    private BasicResolver createIBiblioResolver(String name, URI rootUri) {
        DefaultMavenArtifactRepository repo = new DefaultMavenArtifactRepository(fileResolver);
        repo.setName(name);
        repo.setUrl(rootUri);
        List<DependencyResolver> resolvers = new ArrayList<DependencyResolver>();
        repo.createResolvers(resolvers);
        assert resolvers.size() == 1;
        return (BasicResolver) resolvers.get(0);
    }

    private DependencyResolver createUrlResolver(String name, URI root, Object... jarRepoUrls) {
        URLResolver urlResolver = new UrlWharfResolver();
        urlResolver.setName(name + "_jars");
        urlResolver.setM2compatible(true);
        urlResolver.setChecksums("");
        urlResolver.addArtifactPattern(root.toString() + '/' + ResolverContainer.MAVEN_REPO_PATTERN);
        for (Object jarRepoUrl : jarRepoUrls) {
            urlResolver.addArtifactPattern(fileResolver.resolveUri(jarRepoUrl).toString() + '/' + ResolverContainer.MAVEN_REPO_PATTERN);
        }
        return urlResolver;
    }

    private DualResolver createDualResolver(String name, DependencyResolver ivyResolver, DependencyResolver artifactResolver) {
        DualResolver dualResolver = new DualResolver();
        dualResolver.setName(name);
        dualResolver.setIvyResolver(ivyResolver);
        dualResolver.setArtifactResolver(artifactResolver);
        dualResolver.setDescriptor(DualResolver.DESCRIPTOR_OPTIONAL);
        return dualResolver;
    }

    // todo use MavenPluginConvention pom factory after modularization is done

    public GroovyMavenDeployer createMavenDeployer(String name, MavenPomMetaInfoProvider pomMetaInfoProvider,
                                                   ConfigurationContainer configurationContainer,
                                                   Conf2ScopeMappingContainer scopeMapping, FileResolver fileResolver) {
        PomFilterContainer pomFilterContainer = createPomFilterContainer(
                mavenFactory.createMavenPomFactory(configurationContainer, scopeMapping, fileResolver));
        return new DefaultGroovyMavenDeployer(name, pomFilterContainer, createArtifactPomContainer(
                pomMetaInfoProvider, pomFilterContainer, createArtifactPomFactory()), loggingManagerFactory.create());
    }

    // todo use MavenPluginConvention pom factory after modularization is done

    public MavenResolver createMavenInstaller(String name, MavenPomMetaInfoProvider pomMetaInfoProvider,
                                              ConfigurationContainer configurationContainer,
                                              Conf2ScopeMappingContainer scopeMapping, FileResolver fileResolver) {
        PomFilterContainer pomFilterContainer = createPomFilterContainer(
                mavenFactory.createMavenPomFactory(configurationContainer, scopeMapping, fileResolver));
        return new BaseMavenInstaller(name, pomFilterContainer, createArtifactPomContainer(pomMetaInfoProvider,
                pomFilterContainer, createArtifactPomFactory()), loggingManagerFactory.create());
    }

    public IvyArtifactRepository createIvyRepository() {
        return new DefaultIvyArtifactRepository(fileResolver);
    }

    public MavenArtifactRepository createMavenRepository() {
        return new DefaultMavenArtifactRepository(fileResolver);
    }

    private PomFilterContainer createPomFilterContainer(Factory<MavenPom> mavenPomFactory) {
        return new BasePomFilterContainer(mavenPomFactory);
    }

    private ArtifactPomFactory createArtifactPomFactory() {
        return new DefaultArtifactPomFactory();
    }

    private ArtifactPomContainer createArtifactPomContainer(MavenPomMetaInfoProvider pomMetaInfoProvider, PomFilterContainer filterContainer,
                                                           ArtifactPomFactory pomFactory) {
        return new DefaultArtifactPomContainer(pomMetaInfoProvider, filterContainer, pomFactory);
    }

}
