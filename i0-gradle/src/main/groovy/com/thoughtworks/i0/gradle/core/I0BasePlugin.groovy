package com.thoughtworks.i0.gradle.core

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

class I0BasePlugin implements Plugin<Project> {

    private final Instantiator instantiator

    @Inject
    public I0BasePlugin(Instantiator instantiator) {
        this.instantiator = instantiator;
    }

    @Override
    void apply(Project project) {
        project.convention.plugins.i0 = new I0Convention(project, instantiator)

        project.repositories {
            mavenCentral()
            mavenRepo(url: 'https://github.com/vincentx/i0/raw/master/repository')
        }

        project.afterEvaluate {
            for (facet in project.application.facets)
                facet.configure(project)
        }
    }

    private void configureComponentVersions(Project project) {
        project.extensions.i0Version = "0.2.0-SNAPSHOT"
        project.extensions.guiceVersion = "3.0"
        project.extensions.slf4jVersion = "1.7.2"
        project.extensions.logbackVersion = "1.0.7"
        project.extensions.jacksonVersion = "2.1.1"
        project.extensions.hibernateValidatorVersion = "4.3.0.Final"
    }
}
