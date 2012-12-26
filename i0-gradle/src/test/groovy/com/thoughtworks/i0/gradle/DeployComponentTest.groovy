package com.thoughtworks.i0.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static com.thoughtworks.i0.gradle.Configuration.configurable
import static org.junit.Assert.assertThat
import static org.junit.matchers.JUnitMatchers.hasItems

class DeployComponentTest {
    private Project project
    private def components

    @Before
    public void before() {
        components = [Deploy.provision]

        project = ProjectBuilder.builder().build()
        project.extensions.deploy = configurable(Component.merge(components))
    }

    @Test
    public void should() {
        project.extensions.deploy.provision {
            puppet {
                modules(['a/b'])
            }
        }

        assertThat(project.extensions.deploy.provision.puppet.modules, hasItems("a/b"));
    }
}
