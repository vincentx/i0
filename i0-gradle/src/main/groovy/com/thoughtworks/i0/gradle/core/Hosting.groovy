package com.thoughtworks.i0.gradle.core

import org.gradle.api.Project

interface Hosting extends Facet {
    interface Feature extends Facet {

    }

    void environment(Project project, Environment environment, File root)
}
