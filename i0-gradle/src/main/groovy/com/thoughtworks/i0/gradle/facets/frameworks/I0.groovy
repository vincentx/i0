package com.thoughtworks.i0.gradle.facets.frameworks

import com.thoughtworks.i0.gradle.core.ApplicationFacet
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar

class I0 implements ApplicationFacet {
    private static final def JAVA_COMPONENT_API = [
            'javax.annotation:jsr305:2.0.1',
            'javax.inject:javax.inject:1',
            'javax.validation:validation-api:1.0.0.GA',
            'javax.servlet:servlet-api:3.0'
    ]

    String version = "0.2.0-SNAPSHOT"
    String guice = "3.0"
    String slf4j = "1.7.2"
    String logback = "1.0.7"
    String jackson = "2.1.1"
    String hibernateValdator = "4.3.0.Final"

    @Override
    void generateScaffold(Project project) {
        if (!project.application.scaffold) return

        def folder = project.application.appPackage.replace('.', '/')

        def myStack = project.file("src/main/java/$folder/MyStack.java")
        myStack.parentFile.mkdirs()
        myStack.withWriter {
            it.write("""
            |package $project.application.appPackage;
            |
            |import com.thoughtworks.i0.core.*;
            |
            |@Target({ElementType.TYPE})
            |@Retention(RetentionPolicy.RUNTIME)
            |@Stack
            |${project.application.stackAnnotations.join("\n")}
            |public @interface MyStack {
            |}""".stripMargin().toString())
        }
    }

    @Override
    void configure(Project project) {
        project.application.stackAnnotations.add("@Servlet3")
        project.application.stackAnnotations.add("@GuiceModule")
        project.dependencies {
            compile("com.thoughtworks.i0:i0-core:$version") {
                transitive = false
            }
            compile("com.google.inject:guice:$guice") {
                transitive = false
            }

            compile("org.slf4j:slf4j-api:$slf4j") {
                transitive = false
            }

            JAVA_COMPONENT_API.each { compile(it) }

            runtime("org.hibernate:hibernate-validator:$hibernateValdator")

            runtime "com.fasterxml.jackson.core:jackson-databind:$jackson"
            runtime "com.fasterxml.jackson.datatype:jackson-datatype-guava:$jackson"
            runtime "com.fasterxml.jackson.module:jackson-module-jaxb-annotations:$jackson"
            runtime "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jackson"

            runtime "ch.qos.logback:logback-classic:$logback"
            runtime "org.slf4j:jul-to-slf4j:$slf4j"
            runtime "org.slf4j:log4j-over-slf4j:$slf4j"

            runtime "com.sun.jersey:jersey-core:1.16"
        }

        project.task('deployJar', type: Jar, dependsOn: project.tasks.getByName('jar'),
                group: "Build", description: "Assembles a deploy jar archive containing the main classes and all dependencies.") {
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
}
