package com.thoughtworks.i0.gradle

import com.thoughtworks.i0.gradle.puppet.Puppet
import com.thoughtworks.i0.gradle.ubuntu.Ubuntu
import com.thoughtworks.i0.gradle.vagrant.Vagrant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

class I0PluginN implements Plugin<Project> {

    private final Instantiator instantiator

    @Inject
    public I0PluginN(Instantiator instantiator) {
        this.instantiator = instantiator;
    }

    @Override
    void apply(Project project) {
        project.convention.plugins.i0 = new I0PluginConvention(project, instantiator)

        project.facets {
            provisioner puppet: Puppet
            hosting(vagrant: Vagrant) {
                feature ubuntu: Ubuntu
            }
        }
    }
}
