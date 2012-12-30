package com.thoughtworks.i0.gradle.facets.frameworks

import com.thoughtworks.i0.gradle.core.ApplicationFacet
import org.gradle.api.Project

class HibernateJpa implements ApplicationFacet {
    String version = "4.1.7.Final"
    String flyway = "2.0.2"

    @Override
    void generateScaffold(Project project) {
    }

    @Override
    void configure(Project project) {
        I0 i0 = project.application.find(I0)
        project.dependencies {
            compile("com.thoughtworks.i0:i0-persist-jpa:$i0.version") {
                transitive = false
            }
            compile "org.hibernate.javax.persistence:hibernate-jpa-2.0-api:1.0.1.Final"

            runtime "com.googlecode.flyway:flyway-core:$flyway"
            runtime "org.hibernate:hibernate-entitymanager:$version"
            runtime "com.google.inject.extensions:guice-persist:$i0.guice"
        }
    }
}
