package com.thoughtworks.i0.gradle.facets

import com.thoughtworks.i0.gradle.core.ApplicationFacet
import com.thoughtworks.i0.gradle.core.Environment
import com.thoughtworks.i0.gradle.core.EnvironmentSet
import com.thoughtworks.i0.gradle.core.Provisioner
import com.thoughtworks.i0.gradle.facets.puppet.ModuleServerSet
import com.thoughtworks.i0.gradle.facets.puppet.ModuleSet

class Puppet implements Provisioner {
    String name = "puppet"
    ModuleServerSet servers = new ModuleServerSet()

    @Override
    void configure(EnvironmentSet environments) {
        environments.extensions.add("moduleServers", servers)
    }

    @Override
    void configure(Environment environment) {
        environment.extensions.create("modules", ModuleSet)
    }

    @Override
    boolean configure(ApplicationFacet facet, Environment environment) {
        try {
            facet.puppet(environment.modules)
            return true
        } catch (MissingMethodException e) {
            return false
        }
    }
}
