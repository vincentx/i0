package com.thoughtworks.i0.gradle.core

import org.gradle.api.Project

interface Provisioner extends Facet {
    String getName()

    void configure(EnvironmentSet environments)

    void configure(Environment environment)

    boolean configure(ApplicationFacet facet, Environment environment)

    void resolve(Project project, Environment environment, File root)

    void generateScaffold(Project project)
}
