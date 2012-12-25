package com.thoughtworks.i0.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static com.thoughtworks.i0.gradle.Configuration.configurable
import static com.thoughtworks.i0.gradle.Configuration.nullable
import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.assertThat

class ComponentTest {
    private Project project

    @Before
    public void before() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    public void should_call_handler_to_configure_project() {
        def test = new Component("stack", "test")
        project.extensions.stack = configurable(Component.merge([test]))

        test.extend("component", nullable(version: 10)) { Project p, c ->
            p.extensions.version = c.version
        }

        project.extensions.stack.test {
            component {
                version 15
            }
        }

        test.configure(project)

        assertThat(project.extensions.version, is(15))
    }

    @Test
    public void should_extend_multi_layer_extension() {
        def component = new Component(["stack", "persistence"], "jpa")
        project.extensions.stack = configurable(Component.merge([component]))
        component.extend("hibernate", nullable(version: 10)) { Project p, c ->
            p.extensions.version = c.version
        }
        project.extensions.stack.persistence {
            jpa {
                hibernate {
                    version 15
                }
            }
        }

        component.configure(project)

        assertThat(project.extensions.version, is(15))
    }

    @Test
    public void should_merge_component_configurations() {
        def jpaComponent = new Component(["stack", "persistence"], "jpa")
        def documentComponent = new Component(["stack", "persistence"], "document")

        project.extensions.stack = configurable(Component.merge([jpaComponent, documentComponent]))

        jpaComponent.extend("hibernate", nullable(version: 10)) { Project p, c ->
            p.extensions.hibernateVersion = c.version
        }
        documentComponent.extend("mango", nullable(version: 10)) { Project p, c ->
            p.extensions.mangoVersion = c.version
        }

        project.extensions.stack.persistence {
            jpa {
                hibernate {
                    version 15
                }
            }
            document {
                mango {
                    version 15
                }
            }
        }

        jpaComponent.configure(project)
        documentComponent.configure(project)

        assertThat(project.extensions.hibernateVersion, is(15))
        assertThat(project.extensions.mangoVersion, is(15))
    }

    @Test
    public void should_merge_component_from_different_levels() {
        def languageComponent = new Component("stack", "language")
        def documentComponent = new Component(["stack", "persistence"], "document")

        project.extensions.stack = configurable(Component.merge([languageComponent, documentComponent]))

        languageComponent.extend("java", nullable(version: 10)) { Project p, c ->
            p.extensions.javaVersion = c.version
        }
        documentComponent.extend("mango", nullable(version: 10)) { Project p, c ->
            p.extensions.mangoVersion = c.version
        }

        project.extensions.stack.language {
            java { version 15}
        }
        project.extensions.stack.persistence {
            document {
                mango {
                    version 15
                }
            }
        }

        languageComponent.configure(project)
        documentComponent.configure(project)


        assertThat(project.extensions.javaVersion, is(15))
        assertThat(project.extensions.mangoVersion, is(15))


    }
}


