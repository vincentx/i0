package com.thoughtworks.i0.gradle.facets.puppet

class Module {
    static def of(String module, String configuration) {
        return of(module, configuration, Integer.MAX_VALUE)
    }

    static def of(String module, String configuration, int priority) {
        def parts = module.split("/")
        return new Module(user: parts.first(), name: module.split("/").last(),
                configuration: configuration, priority: priority)
    }

    def String user
    def String name
    def String configuration
    def int priority

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Module module1 = (Module) o

        if (name != module1.name) return false
        if (user != module1.user) return false

        return true
    }

    int hashCode() {
        int result
        result = user.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}