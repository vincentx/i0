package com.thoughtworks.i0.gradle

import org.gradle.util.ConfigureUtil

class Configuration {
    static def configurable(Map<String, Object> defaultValues) {
        return new Configuration(items: defaultValues, nullable: false, present: false)
    }

    static def nullable(Map<String, Object> defaultValues) {
        return new Configuration(items: defaultValues, nullable: true, present: false)
    }

    Map<String, Object> items
    boolean nullable
    boolean present

    def methodMissing(String name, args) {
        if (items.containsKey(name) && args.length == 1 && args[0] instanceof Closure) {
            items[name].present = true
            return ConfigureUtil.configure(args[0], items[name])
        }
        if (items.containsKey(name) && args.length == 1) {
            items.put(name, args[0])
            return
        }
        throw new IllegalAccessException("No such component: " + name)
    }

    def propertyMissing(String name) {
        if (items.containsKey(name)) {
            return items[name]
        }
        throw new IllegalAccessException("No such component: " + name)
    }

    def __configured() {
        return items.keySet().findAll { items[it].present }
    }
}
