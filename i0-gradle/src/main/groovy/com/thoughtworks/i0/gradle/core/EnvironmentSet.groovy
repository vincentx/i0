package com.thoughtworks.i0.gradle.core

import org.gradle.api.Project
import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ConfigureUtil

class EnvironmentSet extends AbstractNamedDomainObjectContainer<Environment> {
    private final Project project

    public EnvironmentSet(Project project, Instantiator instantiator) {
        super(Environment, instantiator)
        this.project = project
    }

    @Override
    protected Environment doCreate(String name) {
        def environment = instantiator.newInstance(Environment, project, name)
        project.provisioner.configure(environment)
        project.facets.hostings.entrySet().each { registered ->
            environment.metaClass."$registered.key" = { Closure closure ->
                if (environment.hosting != null) throw new IllegalArgumentException("only one hosting strategy allowed")
                environment.hosting = registered.value.create()
                ConfigureUtil.configure(closure, environment.hosting)
            }
        }
        return environment
    }
}
