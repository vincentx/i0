package com.thoughtworks.i0.gradle.facets.frameworks

import com.thoughtworks.i0.gradle.core.ApplicationFacet
import org.gradle.api.Project

class Grizzly implements ApplicationFacet {
    String version = "2.3-rc4"
    String tyrus = "1.0-b11"
    String webSocketApi = "1.0-b12"

    @Override
    void generateScaffold(Project project) {
    }

    @Override
    void configure(Project project) {
        I0 i0 = project.application.find(I0)
        project.dependencies {
            compile("com.thoughtworks.i0:i0-grizzly:$i0.version") {
                transitive = false
            }

            compile("javax.websocket:javax.websocket-api:$webSocketApi")

            runtime "org.glassfish.grizzly:grizzly-http-servlet:$version"
            runtime "org.glassfish.tyrus:tyrus-core:$tyrus"
            runtime "org.glassfish.tyrus:tyrus-container-grizzly:$tyrus"
            runtime "com.google.inject.extensions:guice-servlet:$i0.guice"
        }
    }
}
