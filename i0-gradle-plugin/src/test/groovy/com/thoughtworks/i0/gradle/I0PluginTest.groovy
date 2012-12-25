package com.thoughtworks.i0.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertThat
import static org.junit.matchers.JUnitMatchers.hasItems

class I0PluginTest {
    private Project project;

    @Before
    public void before() {
        project = ProjectBuilder.builder().build()
        project.plugins.apply 'i0'
    }

    @Test
    public void should_add_dependencies_after_project_evaluated() {
        project.extensions.stack.language {
            java { version "1.7" }
        }

        project.evaluate()

        assertThat(dependencies("compile"), hasItems(
                'javax.annotation:jsr305:2.0.1',
                'javax.inject:javax.inject:1',
                'javax.validation:validation-api:1.0.0.GA',
                'javax.servlet:servlet-api:3.0',
                'com.thoughtworks.i0:i0-core:0.2.0-SNAPSHOT',
                'com.google.inject:guice:3.0',
                'org.slf4j:slf4j-api:1.7.2'))

        assertThat(dependencies("runtime"), hasItems("org.hibernate:hibernate-validator:4.3.0.Final",
                "com.fasterxml.jackson.core:jackson-databind:2.1.1",
                "com.fasterxml.jackson.datatype:jackson-datatype-guava:2.1.1",
                "com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.1.1",
                "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.1.1",
                "ch.qos.logback:logback-classic:1.0.7", "org.slf4j:jul-to-slf4j:1.7.2",
                "org.slf4j:log4j-over-slf4j:1.7.2"))
    }

    private def dependencies(name) {
        project.configurations."$name".allDependencies.collect { "$it.group:$it.name:$it.version".toString() }
    }
}
