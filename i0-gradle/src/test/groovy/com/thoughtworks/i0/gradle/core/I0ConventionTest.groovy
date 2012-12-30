package com.thoughtworks.i0.gradle.core

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.util.ConfigureUtil
import org.junit.Before
import org.junit.Test

import static org.hamcrest.CoreMatchers.is
import static org.junit.Assert.assertThat
import static org.junit.matchers.JUnitMatchers.hasItems

class I0ConventionTest {

    private Project project

    @Before
    public void before() {

        project = ProjectBuilder.builder().build()
    }

    @Test
    public void provisioner_should_be_able_to_add_configuration_to_environments() {
        buildScript {
            apply plugin: I0BasePlugin

            facets {
                provisioner dummy: DummyProvisioner
            }

            deployment(provision: 'dummy') {
                configurationAddedByDummy {
                    content = "content"
                }
            }
        }

        assertThat(project.environments.readOnlyAddedByDummy, is("dummy"))
        assertThat(project.environments.configurationAddedByDummy.content, is("content"))
    }

    @Test
    public void provisioner_should_be_able_to_add_configuration_to_environment() {
        buildScript {
            apply plugin: I0BasePlugin

            facets {
                provisioner dummy: DummyProvisioner
            }

            deployment(provision: 'dummy') {
                local {
                    configurationAddedByDummy {
                        content = "content"
                    }
                }
            }
        }

        assertThat(project.environments.local.readOnlyAddedByDummy, is("dummy"))
        assertThat(project.environments.local.configurationAddedByDummy.content, is("content"))
    }

    @Test
    public void should_add_hosting_to_environment() {
        buildScript {
            apply plugin: I0BasePlugin

            facets {
                provisioner dummy: DummyProvisioner
                hosting fancy: FancyHosting, plain: PlainHosting
            }

            deployment(provision: 'dummy') {
                local {
                    fancy {
                        fancyProperty = "local"
                        block {
                            content = "local"
                        }
                    }
                }

                ci {
                    plain {
                        plainProperty = "ci"
                        block {
                            content = "ci"
                        }
                    }
                }
            }

            assertThat(project.environments.local.hosting.fancyProperty, is("local"))
            assertThat(project.environments.local.hosting.fancyBlock.content, is("local"))
            assertThat(project.environments.ci.hosting.plainProperty, is("ci"))
            assertThat(project.environments.ci.hosting.plainBlock.content, is("ci"))
        }
    }

    @Test
    public void should_add_hosting_features_to_environment() {
        buildScript {
            apply plugin: I0BasePlugin

            facets {
                provisioner dummy: DummyProvisioner
                hosting(fancy: FancyHosting, plain: PlainHosting) {
                    feature databse: Database
                }
            }

            deployment(provision: 'dummy') {
                local {
                    fancy {
                        fancyProperty = "local"
                        block {
                            content = "local"
                        }
                        databse {
                            provider = "h2"
                        }
                    }
                }

                ci {
                    plain {
                        plainProperty = "ci"
                        block {
                            content = "ci"
                        }
                        databse {
                            provider = "mysql"
                        }
                    }
                }
            }
        }

        assertThat(project.environments.local.hosting.features.size(), is(1))
        assertThat(project.environments.local.hosting.features[0].provider, is("h2"))
        assertThat(project.environments.ci.hosting.features.size(), is(1))
        assertThat(project.environments.ci.hosting.features[0].provider, is("mysql"))
    }

    @Test
    public void should_add_application_fact_to_application() {
        buildScript {
            apply plugin: I0BasePlugin

            facets {
                application {
                    langauge(fancy: FancyLanguage)
                }
            }

            application {
                langauge {
                    fancy { version = "1.5" }
                }
            }
        }

        assertThat(project.application.facets.size(), is(1))
        assertThat(project.application.find(FancyLanguage).version, is("1.5"))
    }

    @Test
    public void should_add_nested_application_fact_to_application() {
        buildScript {
            apply plugin: I0BasePlugin

            facets {
                application {
                    persistence {
                        jpa(fancy: FancyPersistence)
                    }
                }
            }

            facets {
                application {
                    persistence {
                        graph(fancy: FancyPersistence)
                    }
                }

            }

            application {
                persistence {
                    jpa {
                        fancy { provider = "orm" }
                    }
                    graph {
                        fancy { provider = "neo4j" }
                    }
                }
            }
        }

        assertThat(project.application.facets.size(), is(2))
        assertThat(project.application.facets.values()*.provider, hasItems("orm", "neo4j"))
    }

    @Test
    public void should_override_configure_for_application_facet() {
        buildScript {
            apply plugin: I0BasePlugin

            facets {
                application {
                    persistence {
                        jpa(fancy: FancyPersistence)
                    }
                }
            }

            application {
                persistence {
                    jpa {
                        fancy { provider = "orm" }
                    }
                }
            }

            application {
                persistence {
                    jpa {
                        fancy { provider = "neo4j" }
                    }
                }
            }
        }

        assertThat(project.application.facets.size(), is(1))
        assertThat(project.application.find(FancyPersistence).provider, is("neo4j"))
    }

    private def buildScript(Closure closure) {
        ConfigureUtil.configure(closure, project)
    }

    static class DummyProvisioner implements Provisioner {

        @Override
        String getName() {
            return null
        }

        @Override
        boolean configure(ApplicationFacet facet, Environment environment) {
            return true
        }

        @Override
        void resolve(Project project, Environment environment, File root) {
        }

        @Override
        void generateScaffold(Project project) {
        }

        @Override
        void configure(EnvironmentSet environments) {
            environments.extensions.add("readOnlyAddedByDummy", "dummy")
            environments.extensions.create("configurationAddedByDummy", Dummy)
        }

        @Override
        void configure(Environment environment) {
            environment.extensions.add("readOnlyAddedByDummy", "dummy")
            environment.extensions.create("configurationAddedByDummy", Dummy)
        }
    }

    static class Dummy {
        String content
    }

    static class FancyHosting implements Hosting {
        String fancyProperty

        FancyBlock fancyBlock = new FancyBlock()

        def block(Closure closure) {
            ConfigureUtil.configure(closure, fancyBlock)
        }

        @Override
        void environment(Project project, Environment environment, File root) {
        }
    }

    static class FancyBlock {
        String content
    }

    static class PlainHosting implements Hosting {
        String plainProperty

        PlainBlock plainBlock = new PlainBlock()

        def block(Closure closure) {
            ConfigureUtil.configure(closure, plainBlock)
        }

        @Override
        void environment(Project project, Environment environment, File root) {
        }
    }

    static class PlainBlock {
        String content
    }

    static class Database implements Hosting.Feature {
        String provider
    }

    static class FancyLanguage implements ApplicationFacet {
        String version

        @Override
        void generateScaffold(Project project) {
        }

        @Override
        void configure(Project project) {
        }
    }

    static class FancyPersistence implements ApplicationFacet {
        String provider

        @Override
        void generateScaffold(Project project) {
        }

        @Override
        void configure(Project project) {
        }
    }
}
