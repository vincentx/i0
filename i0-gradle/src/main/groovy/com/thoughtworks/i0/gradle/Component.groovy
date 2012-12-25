package com.thoughtworks.i0.gradle

import org.gradle.api.Project

import static com.thoughtworks.i0.gradle.Configuration.nullable

class Component {
    private Map<String, Object> configuration = new HashMap<>()
    private Map<String, Closure> handlers = new HashMap<>()
    private Map<String, Object> extensions = new HashMap<>()
    private def aspects
    private String name

    Component(String aspect, String name) {
        this.name = name
        this.aspects = [aspect]
        this.configuration[name] = nullable(extensions)
    }

    Component(aspects, String name) {
        this.name = name
        this.aspects = aspects
        this.configuration[name] = nullable(extensions)
    }

    Component(aspects, String name, extensions) {
        this.name = name
        this.aspects = aspects
        this.extensions = extensions
        this.configuration[name] = nullable(this.extensions)
    }

    static def merge(components) {
        def partition = components.groupBy { it.aspects }
        if (partition.size() == 1 && partition.keySet().toList()[0].size() == 1) {
            return partition.values().toList()[0].inject([:]) { r, i -> r.putAll(i.configuration()); r }
        }

        return merge(partition.entrySet().collect {
            if (it.key.size > 1) {
                def aspects = it.key.subList(0, it.key.size() - 1)
                def configuration = it.value.inject([:]) { r, i -> r.putAll(i.configuration()); r }
                new Component(aspects, it.key.last(), configuration)
            } else it.value
        }.flatten())
    }

    def configuration() {
        return configuration
    }

    def configure(Project project) {
        def extension = project.extensions
        for (aspect in aspects)
            extension = extension."$aspect"
        for (component in extension."$name".__configured())
            handlers[component](project, extension."$name"."$component")
    }

    def extend(String name, configuration, Closure handler) {
        this.extensions.put(name, configuration)
        this.handlers.put(name, handler)
        return this
    }
}
