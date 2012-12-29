package com.thoughtworks.i0.gradle.core

import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ConfigureUtil

class I0Convention {
    Application application
    Provisioner provisioner
    EnvironmentSet environments

    Facets facets

    private Project project
    private Instantiator instantiator

    I0Convention(Project project, Instantiator instantiator) {
        this.instantiator = instantiator
        this.project = project
        this.environments = instantiator.newInstance(EnvironmentSet, this, instantiator)
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

    def application(Closure closure) {
        if (application == null) {
            application = instantiator.newInstance(Application)
            facets.applicationFacets.entrySet().each {
                def category = configureCategories(it.key)
                def categories = it.key.join("/")
                it.value.entrySet().each {
                    category.metaClass."$it.key" = { Closure config ->
                        if (!application.facets.containsKey("$categories/$it.key")) {
                            def facet = instantiator.newInstance(it.value)
                            application.facets.put("$categories/$it.key", facet)
                        }
                        ConfigureUtil.configure(config, application.facets.get("$categories/$it.key"))
                    }
                }
            }
        }
        ConfigureUtil.configure(closure, application)
    }

    static class Category {
    }

    private def configureCategories(categories) {
        return categories.inject(application) { r, i ->
            def extension = r.extensions.findByName(i)
            if (extension == null) r.extensions.create(i, Category)
            r.extensions.getByName(i)
        }
    }
}








