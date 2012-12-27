package com.thoughtworks.i0.gradle.puppet

import com.thoughtworks.i0.gradle.core.Environment
import com.thoughtworks.i0.gradle.core.EnvironmentSet
import com.thoughtworks.i0.gradle.core.Provisioner

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
}
