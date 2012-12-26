package com.thoughtworks.i0.gradle.puppet

import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine

class ModuleServers {
    def Set<ModuleServer> servers = new HashSet<>()

    def puppetForge() {
        servers.add(new ModuleServer(url: 'http://forge.puppetlabs.com', fetchUriTemplate: '/users/$user/modules/$module/releases/find.json'))
    }

    def forgeServer(url, template) {
        servers.add(new ModuleServer(url: url, fetchUriTemplate: template))
    }
}

class ModuleServer {
    def String url
    def String fetchUriTemplate

    def download(downloader, Module dependency, temporaryDir) {
        try {
            def engine = new SimpleTemplateEngine()
            def fetchUri = engine.createTemplate(fetchUriTemplate).make([
                    "user": dependency.user, "module": dependency.name
            ])
            URLConnection connection = new URL("$url$fetchUri").openConnection()
            def module = new JsonSlurper().parse(new InputStreamReader(connection.inputStream))
            def file = module.file.split("/").last()
            downloader.get(src: "$url$module.file", verbose: true, dest: "$temporaryDir/$file")
            return file
        } catch (Throwable e) {
            return null
        }
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ModuleServer that = (ModuleServer) o

        if (fetchUriTemplate != that.fetchUriTemplate) return false
        if (url != that.url) return false

        return true
    }

    int hashCode() {
        int result
        result = url.hashCode()
        result = 31 * result + fetchUriTemplate.hashCode()
        return result
    }
}

class Modules {
    def Set<Module> modules = new HashSet<>();

    def module(module) {
        modules.add(Module.of(module))
    }
}

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