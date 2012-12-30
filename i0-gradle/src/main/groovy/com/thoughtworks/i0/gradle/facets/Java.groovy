package com.thoughtworks.i0.gradle.facets

import com.thoughtworks.i0.gradle.core.ApplicationFacet
import com.thoughtworks.i0.gradle.facets.puppet.Module
import com.thoughtworks.i0.gradle.facets.puppet.ModuleSet
import org.gradle.api.Project

class Java implements ApplicationFacet {
    String version = "1.7"
    String guava = "13.0.1"

    @Override
    void generateScaffold(Project project) {
    }

    @Override
    void configure(Project project) {
        project.dependencies {
            project.extensions.sourceCompatibility = version

            project.dependencies {
                compile "com.google.guava:guava:$guava"
            }
        }

        for (environment in project.environments)
            if (project.provisioner.configure(this, environment))
                project.logger.warn("Java enabled for $environment.name")
    }

    def puppet(ModuleSet modules) {
        modules.add(Module.of('puppetlabs/stdlib', ""))
        modules.add(Module.of('puppetlabs/java', "include java"))
    }
}
