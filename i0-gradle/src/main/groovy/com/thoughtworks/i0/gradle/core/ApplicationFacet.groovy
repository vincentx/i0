package com.thoughtworks.i0.gradle.core

import org.gradle.api.Project

interface ApplicationFacet extends Facet{
    void configure(Project project)
}
