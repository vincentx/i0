package com.thoughtworks.i0.gradle.core

interface Provisioner extends Facet {
    String getName()

    void configure(EnvironmentSet environments)

    void configure(Environment environment)
}
