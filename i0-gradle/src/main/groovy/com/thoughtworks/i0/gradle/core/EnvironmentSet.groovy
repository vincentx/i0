package com.thoughtworks.i0.gradle.core

import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ConfigureUtil

class EnvironmentSet extends AbstractNamedDomainObjectContainer<Environment> {
    private final I0Convention convention

    public EnvironmentSet(I0Convention convention, Instantiator instantiator) {
        super(Environment, instantiator)
        this.convention = convention
    }

    @Override
    protected Environment doCreate(String name) {
        def environment = instantiator.newInstance(Environment, name)
        convention.provisioner.configure(environment)
        convention.facets.hostings.entrySet().each { registered ->
            environment.metaClass."$registered.key" = { Closure closure ->
                if (environment.hosting != null) throw new IllegalArgumentException("only one hosting strategy allowed")
                environment.hosting = registered.value.create()
                ConfigureUtil.configure(closure, environment.hosting)
            }
        }
        return environment
    }
}
