package com.thoughtworks.i0.gradle.puppet

import com.thoughtworks.i0.gradle.Environment
import com.thoughtworks.i0.gradle.EnvironmentContainer
import com.thoughtworks.i0.gradle.Provisioner

class Puppet implements Provisioner {
    final String name = "puppet"
    final ModuleServerSet servers = new ModuleServerSet()

    @Override
    void configure(EnvironmentContainer environments) {
        environments.extensions.add("moduleServers", servers)
    }

    @Override
    void configure(Environment environment) {
        environment.extensions.create("modules", ModuleSet)
    }
}
