package com.thoughtworks.i0.gradle.facets

import com.thoughtworks.i0.gradle.core.Hosting
import com.thoughtworks.i0.gradle.facets.puppet.Module
import com.thoughtworks.i0.gradle.facets.puppet.ModuleSet

class Ubuntu implements Hosting.Feature {
    private final Map<String, Object> values = [:]

    def propertyMissing(String name) {
        return values[name]
    }

    def propertyMissing(String name, def arg) {
        values[name] = arg
    }

    def puppet(ModuleSet modules) {
        modules.add(Module.of("puppetlabs/apt"))
    }
}
