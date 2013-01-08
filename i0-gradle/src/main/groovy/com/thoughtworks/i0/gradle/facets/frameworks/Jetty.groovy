package com.thoughtworks.i0.gradle.facets.frameworks

import com.thoughtworks.i0.gradle.core.ApplicationFacet
import org.gradle.api.Project

class Jetty implements ApplicationFacet {
    String version = "9.0.0.M3"
    boolean webSocket = false;

    @Override
    void generateScaffold(Project project) {
    }

    @Override
    void configure(Project project) {
        I0 i0 = project.application.find(I0)
        project.application.stackAnnotations.add("@com.thoughtworks.i0.jetty.EmbeddedJetty")

        project.repositories {
            mavenRepo(url: "http://oss.sonatype.org/content/groups/jetty")
        }

        project.dependencies {
            compile("com.thoughtworks.i0:i0-jetty:$i0.version") {
                transitive = false
            }

            if (webSocket) {
                compile("org.eclipse.jetty.websocket:websocket-servlet:$version")
                runtime("org.eclipse.jetty.websocket:websocket-server:$version")
            }

            runtime "org.eclipse.jetty:jetty-servlet:$version"
            runtime "com.google.inject.extensions:guice-servlet:$i0.guice"

            testCompile "org.eclipse.jetty:jetty-client:$version"
        }
    }
}
