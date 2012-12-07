package com.thoughtworks.i0.plugins.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Jar

class I0Plugin implements Plugin<Project> {

    final static def JAVA_EXTENSIONS = [
            'javax.annotation:jsr305:2.0.1',
            'javax.inject:javax.inject:1',
            'javax.validation:validation-api:1.0.0.GA',
            'com.google.guava:guava:13.0.1'
    ]

    final static def JAVA_COMPONENT_API = [
            'javax.servlet:servlet-api:3.0',
            'javax.ws.rs:jsr311-api:1.1.1',
            'org.hibernate.javax.persistence:hibernate-jpa-2.0-api:1.0.1.Final',
            'com.fasterxml.jackson.core:jackson-annotations:2.1.1'
    ]

    final static def IMPLEMENTATIONS = [
            'org.hibernate:hibernate-entitymanager:4.1.7.Final',
            'org.hibernate:hibernate-validator:4.3.0.Final',
            'com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.1.1'
    ]

    @Override
    void apply(Project project) {
        project.repositories {
            mavenCentral()
            mavenRepo(url: 'https://github.com/vincentx/i0/raw/master/repository')
        }
        project.plugins.apply(JavaPlugin.class)
        project.dependencies {
            JAVA_EXTENSIONS.each { compile it }
            JAVA_COMPONENT_API.each { compile it }
            compile('com.thoughtworks.i0:i0-core:0.1.0') {
                transitive = false
            }

            IMPLEMENTATIONS.each { runtime it }
            runtime('com.thoughtworks.i0:i0-core:0.1.0') {
                transitive = true
            }
        }
        project.configurations.getByName('runtime')
                .exclude(group: 'org.eclipse.jetty', module: 'jetty-project')
                .exclude(group: 'javax.persistence', module: 'persistence-api', version: '1.0')
                .exclude(group: 'javax.servlet', module: 'servlet-api', version: '2.5')

        project.task('init', type: InitTask)

        project.task('deployJar', type: Jar, dependsOn: project.tasks.getByName('jar')) {
            baseName = project.name + '-deploy'

            from {project.configurations.runtime.collect { project.zipTree it }} {
                excludes ['META-INF/MANIFEST.MF', '**/*.RSA', '**/*.SF', '**/*.DSA']
            }
            from {project.configurations.default.allArtifacts.files.collect { project.zipTree it} }

            manifest {
                attributes 'Main-Class': 'com.thoughtworks.i0.Launcher'
            }
        }
    }
}
