package com.thoughtworks.i0.gradle

import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine
import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ConfigureUtil

class I0PluginConvention {
    Provisioner provisioner
    EnvironmentContainer environments

    Map environmentProviders = [:]
    Map environmentFacets = [:]
    Map provisioners = [:]

    private Project project

    I0PluginConvention(Project project, Instantiator instantiator) {
        this.project = project
        this.environments = instantiator.newInstance(EnvironmentContainer, project, instantiator)
    }

    def deployment(Closure closure) {
        deployment([provision: 'puppet'], closure)
    }

    def deployment(Map<String, Object> config, Closure closure) {
        provisioner = provisioners[config["provision"] ?: "puppet"]
        provisioner.configure(environments)
        ConfigureUtil.configure(closure, environments)
    }
}

interface Provisioner {
    String getName()

    void configure(EnvironmentContainer environments)

    void configure(Environment environment)
}


class EnvironmentContainer extends AbstractNamedDomainObjectContainer<Environment> {
    private final Project project

    public EnvironmentContainer(Project project, Instantiator instantiator) {
        super(Environment, instantiator)
        this.project = project
    }

    @Override
    protected Environment doCreate(String name) {
        def environment = this.extensions.create("name", Environment, project, name)
        project.provisioner.configure(environment)
        return environment
    }
}

class Environment implements Named {
    String name
    EnvironmentProvider provider

    private final Project project

    Environment(Project project, String name) {
        this.project = project
        this.name = name
    }

    def methodMissing(String name, args) {
        if (project.environmentProviders.containsKey(name)) {
            provider = project.environmentProviders[name](project, this)
            if (args.length == 1 && args[0] instanceof Closure)
                ConfigureUtil.configure(args[0], provider)
        }
    }
}

interface EnvironmentProvider {

}

class Vagrant implements EnvironmentProvider {
    final String name = "vagrant"
    final Map<Integer, Integer> ports = [:]
    final Map<String, EnvironmentFacet> facets = [:]
    private final Environment environment
    private final Project project

    Vagrant(Project project, Environment environment) {
        this.project = project
        this.environment = environment
    }

    def forward(int from, int to) {
        ports[from] = to
    }

    def methodMissing(String name, args) {
        if (project.environmentFacets.containsKey(name)) {
            def facet = project.environmentFacets[name](project, this)
            if (args.length == 1 && args[0] instanceof Closure)
                ConfigureUtil.configure(args[0], facet)
            facets[facet.category] = facet
        }
    }
}

interface EnvironmentFacet {

}

class Ubuntu implements EnvironmentFacet {
    final String category = 'os'
    private final Project project
    private final EnvironmentProvider provider
    private final Map<String, Object> values = [:]

    Ubuntu(Project project, EnvironmentProvider provider) {
        this.project = project
        this.provider = provider
    }

    def propertyMissing(String name) {
        return values[name]
    }

    def propertyMissing(String name, def arg) {
        values[name] = arg
    }
}