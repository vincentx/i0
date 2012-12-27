package com.thoughtworks.i0.gradle.core

import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil


class Environment implements Named {
    String name
    Hosting hosting

    private final Project project

    Environment(Project project, String name) {
        this.project = project
        this.name = name
    }
}
