package com.thoughtworks.i0.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

import static com.thoughtworks.i0.gradle.Configuration.nullable

class Stack {
    static Component language = new Component("stack", "language").extend("java", nullable(
            version: "1.7",
            guava: "13.0.1",
            libraries: []
    ), Stack.&java)

    static Component embedded = new Component("stack", "embedded").extend("jetty", nullable(
            version: "9.0.0.M3"
    ), Stack.&jetty)

    static Component restful = new Component("stack", "restful").extend("jersey", nullable(
            version: "1.16",

    ), Stack.&jersey)

    static Component jpa = new Component(["stack", "persistence"], "jpa").extend("hibernate", nullable(
            version: "4.1.7.Final",
            flyway: "2.0.2"
    ), Stack.&hibernate)



    static def java(Project project, java) {
        project.dependencies {
            project.plugins.apply(JavaPlugin.class)
            project.extensions.sourceCompatibility = java.version

            project.dependencies {
                compile "com.google.guava:guava:$java.guava"
                java.libraries.each { compile it }
            }
        }
    }

    static def jetty(Project project, jetty) {
        project.dependencies {
            compile("com.thoughtworks.i0:i0-jetty:$project.extensions.i0Version") {
                transitive = false
            }

            runtime "org.eclipse.jetty:jetty-servlet:$jetty.version"
            runtime "com.google.inject.extensions:guice-servlet:$project.extensions.guiceVersion"

            testCompile "org.eclipse.jetty:jetty-client:$jetty.version"
        }
    }

    static def jersey(Project project, jersey) {
        project.dependencies {
            compile("com.thoughtworks.i0:i0-jersey:$project.extensions.i0Version") {
                transitive = false
            }

            runtime "com.sun.jersey:jersey-core:$jersey.version"
            runtime "com.sun.jersey:jersey-server:$jersey.version"
            runtime "com.sun.jersey:jersey-servlet:$jersey.version"
            runtime "com.sun.jersey.contribs:jersey-guice:$jersey.version"

            runtime "com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:$project.extensions.jacksonVersion"

            testCompile "com.sun.jersey:jersey-client:$jersey.version"
        }
    }

    static def hibernate(Project project, hibernate) {
        project.dependencies {
            compile("com.thoughtworks.i0:i0-persist-jpa:$project.extensions.i0Version") {
                transitive = false
            }
            compile "org.hibernate.javax.persistence:hibernate-jpa-2.0-api:1.0.1.Final"

            runtime "com.googlecode.flyway:flyway-core:$hibernate.flyway"
            runtime "org.hibernate:hibernate-entitymanager:$hibernate.version"
            runtime "com.google.inject.extensions:guice-persist:$project.extensions.guiceVersion"
        }
    }
}
