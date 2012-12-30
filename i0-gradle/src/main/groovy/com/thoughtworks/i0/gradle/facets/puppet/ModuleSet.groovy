package com.thoughtworks.i0.gradle.facets.puppet

class ModuleSet {
    @Delegate Set<Module> modules = new HashSet<>()

    private int priority = 1

    def module(String notation) {
        module(notation, "")
    }

    def module(String notation, String configuration) {
        add(Module.of(notation, configuration))
    }

    def prerequisite(String notation, String configuration) {
        add(Module.of(notation, configuration, priority++))
    }

    def aptupdate() {
        def config = "exec { \"apt-update\":\n" +
                "       command => \"/usr/bin/apt-get update\"\n" +
                "   }\n" +
                "   Exec[\"apt-update\"] -> Package <| |>"
        add(Module.of('puppetlabs/apt', config, priority++))
    }
}
