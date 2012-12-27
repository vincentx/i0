package com.thoughtworks.i0.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static com.thoughtworks.i0.gradle.Configuration.configurable
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.notNullValue
import static org.junit.Assert.assertThat
import static org.junit.matchers.JUnitMatchers.hasItems

class DeployComponentTest {
    private Project project
    private def components

    @Before
    public void before() {
        components = [Deploy.provision, Deploy.localEnvironment]

        project = ProjectBuilder.builder().build()
        project.extensions.deploy = configurable(Component.merge(components))
        project.plugins.apply(JavaPlugin)
    }

    @Test
    public void should_config_puppet_modules() {
        project.extensions.deploy.provision {
            puppet {
                modules(['a/b'])
            }
        }

        assertThat(project.extensions.deploy.provision.puppet.modules, hasItems("a/b"));
    }

    @Test
    public void should_add_puppet_source_sets() {
        project.extensions.deploy.provision {
            puppet {}
        }

        Deploy.provision.configure(project)
    }

    @Test
    public void should_config_vagrant_properties() {
        project.extensions.deploy.environment {
            local {
                vagrant {
                    box 'precise64'
                }
            }
        }

        assertThat(project.extensions.deploy.environment.local.vagrant.box, equalTo("precise64"));
    }

}
