package com.thoughtworks.i0.plugins.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.plugins.JavaPlugin

class I0Plugin implements Plugin<Project> {

    final static def JAVAX_DEPENDENCIES = ['javax.servlet:servlet-api:3.0', 'javax.inject:javax.inject:1',
            'javax.annotation:jsr-305:2.0.1']

    final static def FUNCTIONAL_JAVA = ['com.google.guava:guava:13.0.1']

    @Override
    void apply(Project project) {
        project.repositories {
            mavenCentral()
            mavenRepo(url: 'https://github.com/vincentx/i0/raw/master/repository')
        }
        project.plugins.apply(JavaPlugin.class)
        project.dependencies {
            JAVAX_DEPENDENCIES.each {compile it}
            FUNCTIONAL_JAVA.each {compile it}
            compile('com.thoughtworks.i0:i0-core:0.1.0') {
                transitive = false
            }
        }
    }
}
