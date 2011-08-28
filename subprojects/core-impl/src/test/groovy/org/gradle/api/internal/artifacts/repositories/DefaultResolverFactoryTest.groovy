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

package org.gradle.api.internal.artifacts.repositories

import org.gradle.api.InvalidUserDataException
import org.gradle.api.artifacts.ResolverContainer
import org.gradle.api.artifacts.maven.MavenFactory
import org.gradle.api.internal.Factory
import org.gradle.api.internal.artifacts.ivyservice.LocalFileRepositoryCacheManager
import org.gradle.api.internal.artifacts.publish.maven.LocalMavenCacheLocator
import org.gradle.api.internal.file.FileResolver
import org.gradle.util.JUnit4GroovyMockery
import org.hamcrest.Matchers
import org.jmock.integration.junit4.JMock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.apache.ivy.plugins.resolver.*

/**
 * @author Hans Dockter
 */
@RunWith(JMock.class)
class DefaultResolverFactoryTest {
    static final URI RESOLVER_URL = new URI('http://a.b.c/')
    static final IBiblioResolver TEST_RESOLVER = new IBiblioResolver()
    static {
        TEST_RESOLVER.name = 'ivyResolver'
    }

    static final String TEST_REPO_NAME = 'reponame'
    static final String TEST_REPO = 'http://www.gradle.org'
    static final URI TEST_REPO_URL = new URI('http://www.gradle.org/')
    static final URI TEST_REPO2_URL = new URI('http://www.gradleware.com/')

    final JUnit4GroovyMockery context = new JUnit4GroovyMockery()
    final LocalMavenCacheLocator localMavenCacheLocator = context.mock(LocalMavenCacheLocator.class)
    final FileResolver fileResolver = context.mock(FileResolver.class)
    final DefaultResolverFactory factory = new DefaultResolverFactory(context.mock(Factory.class), context.mock(MavenFactory.class), localMavenCacheLocator, fileResolver)

    @Before public void setup() {
        context.checking {
            allowing(fileResolver).resolveUri('uri');
            will(returnValue(RESOLVER_URL))
            allowing(fileResolver).resolveUri(TEST_REPO);
            will(returnValue(TEST_REPO_URL))
            allowing(fileResolver).resolveUri('uri2');
            will(returnValue(TEST_REPO2_URL))
            allowing(fileResolver).resolveUri(withParam(Matchers.instanceOf(URI)));
            will { uri -> return uri }
        }
    }

    @Test public void testCreateResolverWithStringDescription() {
        checkMavenResolver(factory.createResolver('uri'), 'uri', RESOLVER_URL)
    }

    @Test public void testCreateResolverWithMapDescription() {
        checkMavenResolver(factory.createResolver([name: 'name', url: 'uri']), 'name', RESOLVER_URL)
    }

    @Test public void testCreateResolverWithResolverDescription() {
        DependencyResolver resolver = factory.createResolver(TEST_RESOLVER)
        assert resolver.is(TEST_RESOLVER)
    }

    @Test(expected = InvalidUserDataException) public void testCreateResolverForUnknownDescription() {
        def someIllegalDescription = new NullPointerException()
        factory.createResolver(someIllegalDescription)
    }

    private void checkMavenResolver(IBiblioResolver resolver, String name, URI url) {
        assert url.toString() == resolver.root
        assert name == resolver.name
        assert resolver.allownomd
    }

    @Test
    public void testCreateMavenRepoWithAdditionalJarUrls() {
        DualResolver dualResolver = factory.createMavenRepoResolver(TEST_REPO_NAME, TEST_REPO, 'uri2')
        assert dualResolver.allownomd
        checkIBiblio(dualResolver.ivyResolver, "_poms")
        URLResolver urlResolver = dualResolver.artifactResolver
        assert urlResolver.m2compatible
        assert urlResolver.artifactPatterns.contains("$TEST_REPO_URL/$ResolverContainer.MAVEN_REPO_PATTERN" as String)
        assert urlResolver.artifactPatterns.contains("$TEST_REPO2_URL/$ResolverContainer.MAVEN_REPO_PATTERN" as String)
        assert "${TEST_REPO_NAME}_jars" == urlResolver.name
    }

    @Test
    public void testCreateMavenRepoWithNoAdditionalJarUrls() {
        checkIBiblio(factory.createMavenRepoResolver(TEST_REPO_NAME, TEST_REPO_URL), "")
    }

    private void checkIBiblio(IBiblioResolver iBiblioResolver, String expectedNameSuffix) {
        assert iBiblioResolver.usepoms
        assert iBiblioResolver.m2compatible
        assert iBiblioResolver.allownomd
        assert TEST_REPO_URL.toString() == iBiblioResolver.root
        assert ResolverContainer.MAVEN_REPO_PATTERN == iBiblioResolver.pattern
        assert "${TEST_REPO_NAME}$expectedNameSuffix" == iBiblioResolver.name
    }

    @Test public void testCreateFlatDirResolver() {
        File dir1 = new File('/rootFolder')
        File dir2 = new File('/rootFolder2')
        context.checking {
            allowing(fileResolver).resolve('a')
            will(returnValue(dir1))
            allowing(fileResolver).resolve('b')
            will(returnValue(dir2))
        }
        String expectedName = 'libs'
        FileSystemResolver resolver = factory.createFlatDirResolver(expectedName, 'a', 'b')
        def expectedPatterns = [
                "$dir1.absolutePath/[artifact]-[revision](-[classifier]).[ext]",
                "$dir1.absolutePath/[artifact](-[classifier]).[ext]",
                "$dir2.absolutePath/[artifact]-[revision](-[classifier]).[ext]",
                "$dir2.absolutePath/[artifact](-[classifier]).[ext]"
        ]
        assert expectedName == resolver.name
        assert [] == resolver.ivyPatterns
        assert expectedPatterns == resolver.artifactPatterns
        assert resolver.allownomd
    }

    @Test public void testCreateLocalMavenRepo() {
        File repoDir = new File(".m2/repository")

        context.checking {
            one(localMavenCacheLocator).getLocalMavenCache()
            will(returnValue(repoDir))
                allowing(fileResolver).resolveUri(repoDir)
                will(returnValue(repoDir.toURI()))
        }

        def repo = factory.createMavenLocalResolver('name')
        assert repo instanceof IBiblioResolver
        assert repo.root == repoDir.toURI().getPath() + '/'
        assert repo.repositoryCacheManager instanceof LocalFileRepositoryCacheManager
    }

    @Test public void createIvyRepository() {
        def repo = factory.createIvyRepository()
        assert repo instanceof DefaultIvyArtifactRepository
    }

    @Test public void createMavenRepository() {
        def repo = factory.createMavenRepository()
        assert repo instanceof DefaultMavenArtifactRepository
    }
}
