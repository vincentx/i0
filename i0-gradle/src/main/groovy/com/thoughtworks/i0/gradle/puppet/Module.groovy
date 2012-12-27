package com.thoughtworks.i0.gradle.puppet

class Module {
    static def of(String module) {
        def parts = module.split("/")
        return new Module(user: parts.first(), name: module.split("/").last())
    }

    def String user
    def String name

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