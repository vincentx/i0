package com.thoughtworks.i0.gradle.core

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

class I0BasePlugin implements Plugin<Project> {

    private final Instantiator instantiator

    @Inject
    public I0BasePlugin(Instantiator instantiator) {
        this.instantiator = instantiator;
    }

    @Override
    void apply(Project project) {
        project.convention.plugins.i0 = new I0Convention(project, instantiator)
    }
}
