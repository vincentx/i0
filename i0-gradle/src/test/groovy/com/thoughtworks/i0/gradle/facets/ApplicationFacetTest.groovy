package com.thoughtworks.i0.gradle.facets

import com.thoughtworks.i0.gradle.core.I0BasePlugin
import com.thoughtworks.i0.gradle.facets.frameworks.HibernateJpa
import com.thoughtworks.i0.gradle.facets.frameworks.I0
import com.thoughtworks.i0.gradle.facets.frameworks.Jersey
import com.thoughtworks.i0.gradle.facets.frameworks.Jetty
import com.thoughtworks.i0.gradle.facets.puppet.Module
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.util.ConfigureUtil
import org.junit.Before
import org.junit.Test

import static org.hamcrest.CoreMatchers.is
import static org.junit.Assert.assertThat
import static org.junit.matchers.JUnitMatchers.hasItem
import static org.junit.matchers.JUnitMatchers.hasItems

class ApplicationFacetTest {
    private Project project

    @Before
    public void before() {
        project = ProjectBuilder.builder().build()
        project.plugins.apply(JavaPlugin)
        project.plugins.apply(I0BasePlugin)
    }

    @Test
    public void java_should_set_source_code_compatibility() {
        def java = new Java(version: "1.6", guava: "2.5")
        java.configure(project)
        assertThat(project.sourceCompatibility, is("1.6"))
    }

    @Test
    public void java_should_declare_guava_dependencies() {
        def java = new Java(version: "1.6", guava: "12.0.0")
        java.configure(project)
        assertThat(dependencies("compile"), hasItem("com.google.guava:guava:12.0.0"))
    }

    @Test
    public void java_should_add_puppet_modules_to_environment() {
        buildScript {
            facets {
                provisioner puppet: Puppet
            }

            deployment(provisioner: 'puppet') {
                local {}
            }
        }
        def java = new Java(version: "1.6", guava: "12.0.0")
        java.configure(project)

        assertThat(project.environments.local.modules.size(), is(2))
        assertThat(project.environments.local.modules, hasItems(Module.of("puppetlabs/stdlib", ""), Module.of("puppetlabs/java", "")))
    }

    @Test
    public void jersey_should_declare_dependencies() {
        buildScript {
            facets {
                application {
                    framework i0: I0
                }
            }

            application {
                framework {
                    i0 {
                        version = "0.2.0-SNAPSHOT"
                        guice = "3.0"
                        jackson = "2.1.1"
                    }
                }
            }
        }

        def jersey = new Jersey()
        jersey.configure(project)

        assertThat(dependencies("compile"), hasItems("com.thoughtworks.i0:i0-jersey:0.2.0-SNAPSHOT",
                "com.sun.jersey:jersey-core:1.16"))

        assertThat(dependencies("runtime"), hasItems("com.sun.jersey:jersey-server:1.16",
                "com.sun.jersey:jersey-servlet:1.16", "com.sun.jersey.contribs:jersey-guice:1.16",
                "com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.1.1"))

        assertThat(dependencies("testCompile"), hasItems("com.sun.jersey:jersey-client:1.16"))
    }

    @Test
    public void jetty_should_declare_dependencies() {
        buildScript {
            facets {
                application {
                    framework i0: I0
                }
            }

            application {
                framework {
                    i0 {
                        version = "0.2.0-SNAPSHOT"
                        guice = "3.0"
                    }
                }
            }
        }

        def jetty = new Jetty()
        jetty.configure(project)

        assertThat(dependencies("compile"), hasItem("com.thoughtworks.i0:i0-jetty:0.2.0-SNAPSHOT"))
        assertThat(dependencies("runtime"), hasItems("org.eclipse.jetty:jetty-servlet:9.0.0.M3", "com.google.inject.extensions:guice-servlet:3.0"))
        assertThat(dependencies("testCompile"), hasItem("org.eclipse.jetty:jetty-client:9.0.0.M3"))
    }

    @Test
    public void hibernate_jpa_should_declare_dependencies() {
        buildScript {
            facets {
                application {
                    framework i0: I0
                }
            }

            application {
                framework {
                    i0 {
                        version = "0.2.0-SNAPSHOT"
                        guice = "3.0"
                    }
                }
            }
        }

        def hibernate = new HibernateJpa()
        hibernate.configure(project)

        assertThat(dependencies("compile"), hasItems("com.thoughtworks.i0:i0-persist-jpa:0.2.0-SNAPSHOT", "org.hibernate.javax.persistence:hibernate-jpa-2.0-api:1.0.1.Final"))
        assertThat(dependencies("runtime"), hasItems("com.googlecode.flyway:flyway-core:2.0.2", "org.hibernate:hibernate-entitymanager:4.1.7.Final", "com.google.inject.extensions:guice-persist:3.0"))
    }

    @Test
    public void i0_should_declare_dependencies() {
        def i0 = new I0()
        i0.configure(project)

        assertThat(dependencies("compile"), hasItems("com.thoughtworks.i0:i0-core:0.2.0-SNAPSHOT",
                "com.google.inject:guice:3.0", "org.slf4j:slf4j-api:1.7.2", "javax.annotation:jsr305:2.0.1",
                "javax.inject:javax.inject:1", "javax.validation:validation-api:1.0.0.GA", "javax.servlet:servlet-api:3.0"))

        assertThat(dependencies("runtime"), hasItems("com.fasterxml.jackson.core:jackson-databind:2.1.1",
                "com.fasterxml.jackson.datatype:jackson-datatype-guava:2.1.1", "com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.1.1",
                "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.1.1", "ch.qos.logback:logback-classic:1.0.7",
                "org.slf4j:jul-to-slf4j:1.7.2", "org.slf4j:log4j-over-slf4j:1.7.2", "com.sun.jersey:jersey-core:1.16"))
    }

    private def buildScript(Closure closure) {
        ConfigureUtil.configure(closure, project)
    }

    private def dependencies(name) {
        project.configurations."$name".allDependencies.collect { "$it.group:$it.name:$it.version".toString() }
    }
}
