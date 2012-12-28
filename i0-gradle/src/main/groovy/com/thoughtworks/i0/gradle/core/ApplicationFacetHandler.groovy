package com.thoughtworks.i0.gradle.core

import org.gradle.util.ConfigureUtil

class ApplicationFacetHandler {
    Facets facets
    def categories = []

    ApplicationFacetHandler(Facets facets) {
        this.facets = facets
    }

    ApplicationFacetHandler(Facets facets, categories) {
        this.facets = facets
        this.categories = categories
    }

    def methodMissing(String category, def args) {
        def key = categories + [category]
        if (args.length == 1 && args[0] instanceof Map<String, Class<? extends ApplicationFacet>>) {
            if (!facets.applicationFacets.containsKey(key)) facets.applicationFacets.put(key, [:])
            args[0].entrySet().each { facets.applicationFacets[key].put(it.key, it.value) }
        }
        if (args.length == 1 && args[0] instanceof Closure) {
            ConfigureUtil.configure(args[0], new ApplicationFacetHandler(facets, key))
        }
    }
}
