package com.thoughtworks.i0.gradle.facets.puppet


class ModuleServerSet {
    @Delegate def Set<ModuleServer> servers = new HashSet<>()

    def puppetForge() {
        servers.add(new ModuleServer(url: 'http://forge.puppetlabs.com', fetchUriTemplate: '/users/$user/modules/$module/releases/find.json'))
    }

    def server(url, template) {
        servers.add(new ModuleServer(url: url, fetchUriTemplate: template))
    }
}
