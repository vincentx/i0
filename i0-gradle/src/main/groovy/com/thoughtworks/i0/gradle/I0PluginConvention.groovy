package com.thoughtworks.i0.gradle

import com.thoughtworks.i0.gradle.core.EnvironmentSet
import com.thoughtworks.i0.gradle.core.Facets
import com.thoughtworks.i0.gradle.core.Provisioner
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ConfigureUtil

class I0PluginConvention {
    Provisioner provisioner
    EnvironmentSet environments

    Facets facets

    private Project project

    I0PluginConvention(Project project, Instantiator instantiator) {
        this.project = project
        this.environments = instantiator.newInstance(EnvironmentSet, project, instantiator)
        this.facets = new Facets(instantiator)
    }

    def deployment(Closure closure) {
        deployment([provision: 'puppet'], closure)
    }

    def facets(Closure closure) {
        ConfigureUtil.configure(closure, facets)
    }

    def deployment(Map<String, Object> config, Closure closure) {
        provisioner = facets.provisioners[config["provision"] ?: "puppet"]
        provisioner.configure(environments)
        ConfigureUtil.configure(closure, environments)
    }
}








