package com.thoughtworks.i0.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.util.ConfigureUtil
import org.junit.Before
import org.junit.Test

import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.CoreMatchers.notNullValue
import static org.junit.Assert.assertThat

class ConfigTest {
    private Project project

    @Before
    public void before() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    public void should_config_environment_with_vagrant_environment_provider() {
        buildScript {
            apply plugin: I0PluginN

            deployment {
                local {
                    vagrant {
                        forward 8001, 8005
                        forward 8080, 8051
                    }
                }
            }
        }
        assertThat(project.environments.size(), is(1))
        assertThat(project.environments.local, notNullValue())
        assertThat(project.environments.local.provider.ports[8001], is(8005))
        assertThat(project.environments.local.provider.ports[8080], is(8051))
    }

    @Test
    public void should_config_vagrant_environment_provider_with_environment_facet() {
        buildScript {
            apply plugin: I0PluginN

            deployment {
                local {
                    vagrant {
                        ubuntu {
                            box = 'precise32'
                            url = 'http://files.vagrantup.com/precise32.box'
                        }
                    }
                }
            }
        }

        assertThat(project.environments.local.provider.facets.size(), is(1))
        assertThat(project.environments.local.provider.facets.os.box, is('precise32'))
        assertThat(project.environments.local.provider.facets.os.url, is('http://files.vagrantup.com/precise32.box'))
    }

    @Test
    public void should_ask_the_provisioner_to_set_environments_config() {
        buildScript {
            apply plugin: I0PluginN

            deployment(provision: 'puppet') {
                moduleServers {
                    puppetForge()
                }
            }
        }

        assertThat(project.provisioner.name, is('puppet'))
        assertThat(project.provisioner.servers.size(), is(1))
        assertThat(project.provisioner.servers.toArray()[0].url, is('http://forge.puppetlabs.com'))
        assertThat(project.provisioner.servers.toArray()[0].fetchUriTemplate, is('/users/$user/modules/$module/releases/find.json'))
    }

    @Test
    public void should_ask_the_provisioner_to_set_environment_config() {
        buildScript {
            apply plugin: I0PluginN

            deployment(provision: 'puppet') {
                local {
                    modules {
                        module('puppetlabs/java')
                    }
                }
            }
        }

        assertThat(project.environments.local.modules.size(), is(1))
        assertThat(project.environments.local.modules.toArray()[0].user, is("puppetlabs"))
        assertThat(project.environments.local.modules.toArray()[0].name, is("java"))
    }

    private def buildScript(Closure closure) {
        ConfigureUtil.configure(closure, project)
    }
}
