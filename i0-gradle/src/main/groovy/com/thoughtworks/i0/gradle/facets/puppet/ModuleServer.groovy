package com.thoughtworks.i0.gradle.facets.puppet

import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine

class ModuleServer {
    def String url
    def String fetchUriTemplate
    private def engine = new SimpleTemplateEngine()

    def download(downloader, dependency, temporaryDir) {
        try {
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
