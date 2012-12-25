package com.thoughtworks.i0.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static com.thoughtworks.i0.gradle.Configuration.configurable
import static org.hamcrest.CoreMatchers.is
import static org.junit.Assert.assertThat
import static org.junit.matchers.JUnitMatchers.hasItem
import static org.junit.matchers.JUnitMatchers.hasItems

class StackComponentTest {
    private Project project
    private def components

    @Before
    public void before() {
        components = [Stack.language, Stack.embedded, Stack.restful, Stack.jpa]

        project = ProjectBuilder.builder().build()
        project.extensions.i0Version = "0.2.0-SNAPSHOT"
        project.extensions.guiceVersion = "3.0"
        project.extensions.jacksonVersion = "2.1.1"
        project.extensions.stack = configurable(Component.merge(components))
        project.plugins.apply(JavaPlugin.class)
    }

    @Test
    public void java_should_set_source_level_to_java_version() {
        project.extensions.stack.language.java { version "1.6" }

        components.each { it.configure(project) }

        assertThat(project.extensions.sourceCompatibility, is("1.6"))
    }

    @Test
    public void java_should_add_guava_as_compile_dependencies() {
        project.extensions.stack.language.java { guava "13.0.1" }

        components.each { it.configure(project) }

        assertThat(dependencies("compile"), hasItem("com.google.guava:guava:13.0.1"))
    }

    @Test
    public void java_should_add_libraries_as_compile_dependencies() {
        project.extensions.stack.language.java { libraries(["test:test:1.0", "test:test:2.0"]) }

        components.each { it.configure(project) }

        assertThat(dependencies("compile"), hasItems("test:test:1.0", "test:test:2.0"))
    }

    @Test
    public void jetty_should_add_dependencies() {
        project.extensions.stack.embedded.jetty { version "9.0.0.M3" }
        components.each { it.configure(project) }

        assertThat(dependencies("compile"), hasItem("com.thoughtworks.i0:i0-jetty:0.2.0-SNAPSHOT"))

        assertThat(dependencies("runtime"), hasItems("org.eclipse.jetty:jetty-servlet:9.0.0.M3",
                "com.google.inject.extensions:guice-servlet:3.0"))

        assertThat(dependencies("testCompile"), hasItem("org.eclipse.jetty:jetty-client:9.0.0.M3"))
    }

    @Test
    public void jersey_should_add_dependencies() {
        project.extensions.stack.restful.jersey {
            version "1.16"
        }
        components.each { it.configure(project) }

        assertThat(dependencies("compile"), hasItem("com.thoughtworks.i0:i0-jersey:0.2.0-SNAPSHOT"))

        assertThat(dependencies("runtime"), hasItems("com.sun.jersey:jersey-core:1.16",
                "com.sun.jersey:jersey-server:1.16", "com.sun.jersey:jersey-servlet:1.16",
                "com.sun.jersey.contribs:jersey-guice:1.16", "com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.1.1"))

        assertThat(dependencies("testCompile"), hasItems("com.sun.jersey:jersey-client:1.16"))
    }

    @Test
    public void jpa_should_add_dependencies() {
        project.extensions.stack.persistence.jpa.hibernate { version "4.1.7.Final" }
        components.each { it.configure(project) }

        assertThat(dependencies("compile"), hasItems("com.thoughtworks.i0:i0-persist-jpa:0.2.0-SNAPSHOT",
                "org.hibernate.javax.persistence:hibernate-jpa-2.0-api:1.0.1.Final"))

        assertThat(dependencies("runtime"), hasItems("com.googlecode.flyway:flyway-core:2.0.2",
                "org.hibernate:hibernate-entitymanager:4.1.7.Final",
                "com.google.inject.extensions:guice-persist:3.0"))
    }


    private def dependencies(name) {
        project.configurations."$name".allDependencies.collect { "$it.group:$it.name:$it.version".toString() }
    }
}
