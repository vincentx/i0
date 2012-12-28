package com.thoughtworks.i0.gradle.ubuntu

import com.thoughtworks.i0.gradle.core.Hosting

class Ubuntu implements Hosting.Feature {
    private final Map<String, Object> values = [:]

    def propertyMissing(String name) {
        return values[name]
    }

    def propertyMissing(String name, def arg) {
        values[name] = arg
    }
}
