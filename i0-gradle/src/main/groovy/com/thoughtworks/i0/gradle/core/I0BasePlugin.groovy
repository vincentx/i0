package com.thoughtworks.i0.gradle.core

import org.gradle.api.Plugin
import org.gradle.api.Project
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
            for (facet in project.application.facets.values())
                facet.configure(project)

            project.application.provisioner.configure(project)

            project.task("generateScaffold", group: "Scaffold", description: "Generates the scaffolding structures and code snippets") << {
                project.application.facets.values().each {
                    it.generateScaffold(project)
                }

                project.provisioner.generateScaffold(project)
            }

            project.task("cleanEnvironments", group: "Deployment", description: "Clean environment configurations") << {
                project.delete(project.file("environments"))
            }

            project.task("prepareEnvironments", group: "Deployment", description: "Prepares environment configurations") << {
                for (environment in project.environments) {
                    project.file("environments/$environment.name").mkdirs()
                    environment.hosting.environment(project, environment, project.file("environments/$environment.name"))
                }
            }

        }
    }
}
