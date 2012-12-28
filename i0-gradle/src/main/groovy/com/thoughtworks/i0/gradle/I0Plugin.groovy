package com.thoughtworks.i0.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Jar

import static com.thoughtworks.i0.gradle.Configuration.configurable

class I0Plugin implements Plugin<Project> {

    static final def JAVA_COMPONENT_API = [
            'javax.annotation:jsr305:2.0.1',
            'javax.inject:javax.inject:1',
            'javax.validation:validation-api:1.0.0.GA',
            'javax.servlet:servlet-api:3.0'
    ]

    @Override
    void apply(Project project) {
        addComponentVersions(project)

        project.extensions.stackComponents = [language: Stack.language, embedded: Stack.embedded,
                restful: Stack.restful, jpa: Stack.jpa]

        project.extensions.deployComponents = [provision: Deploy.provision, local: Deploy.localEnvironment]

        project.extensions.stack = configurable(Component.merge(project.extensions.stackComponents.values()))
        project.extensions.deploy = configurable(Component.merge(project.extensions.deployComponents.values()))

        configRepositories(project)

        project.plugins.apply(JavaPlugin.class)

//        project.facets {
//            provisioner puppet: Puppet
//            hosting(vagrant: Vagrant) {
//                feature ubuntu: Ubuntu
//            }
//        }


        addTasks(project)

        project.afterEvaluate {
            for (component in project.extensions.deployComponents.values())
                component.configure(project)

            for (component in project.extensions.stackComponents.values())
                component.configure(project)

            it.dependencies {
                compile("com.thoughtworks.i0:i0-core:$project.extensions.i0Version") {
                    transitive = false
                }
                compile("com.google.inject:guice:$project.extensions.guiceVersion") {
                    transitive = false
                }

                compile("org.slf4j:slf4j-api:$project.extensions.slf4jVersion") {
                    transitive = false
                }

                JAVA_COMPONENT_API.each { compile(it) }

                runtime("org.hibernate:hibernate-validator:$project.extensions.hibernateValidatorVersion")

                runtime "com.fasterxml.jackson.core:jackson-databind:$project.extensions.jacksonVersion"
                runtime "com.fasterxml.jackson.datatype:jackson-datatype-guava:$project.extensions.jacksonVersion"
                runtime "com.fasterxml.jackson.module:jackson-module-jaxb-annotations:$project.extensions.jacksonVersion"
                runtime "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$project.extensions.jacksonVersion"

                runtime "ch.qos.logback:logback-classic:$project.extensions.logbackVersion"
                runtime "org.slf4j:jul-to-slf4j:$project.extensions.slf4jVersion"
                runtime "org.slf4j:log4j-over-slf4j:$project.extensions.slf4jVersion"

                runtime "com.sun.jersey:jersey-core:1.16"
            }
        }
    }

    private void addTasks(Project project) {
        project.task('deployJar', type: Jar, dependsOn: project.tasks.getByName('jar')) {
            baseName = 'deploy'

            from { project.configurations.runtime.collect { project.zipTree it } } {
                exclude 'META-INF/MANIFEST.MF', '**/*.RSA', '**/*.SF', '**/*.DSA'
            }
            from { project.configurations.default.allArtifacts.files.collect { project.zipTree it } }

            manifest {
                attributes 'Main-Class': 'com.thoughtworks.i0.core.Launcher'
            }
        }
    }

    private void configRepositories(Project project) {
        project.repositories {
            mavenCentral()
            mavenRepo(url: 'https://github.com/vincentx/i0/raw/master/repository')
        }
    }

    private void addComponentVersions(Project project) {
        project.extensions.i0Version = "0.2.0-SNAPSHOT"
        project.extensions.guiceVersion = "3.0"
        project.extensions.slf4jVersion = "1.7.2"
        project.extensions.logbackVersion = "1.0.7"
        project.extensions.jacksonVersion = "2.1.1"
        project.extensions.hibernateValidatorVersion = "4.3.0.Final"
    }
}
