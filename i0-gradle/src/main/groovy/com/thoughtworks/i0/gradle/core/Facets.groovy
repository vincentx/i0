package com.thoughtworks.i0.gradle.core

import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ConfigureUtil

class Facets {
    private Instantiator instantiator

    Map<String, Provisioner> provisioners = [:]
    Map<String, HostingFactory> hostings = [:]
    Map<List<String>, Map<String, Class<? extends ApplicationFacet>>> applicationFacets = [:]

    Facets(Instantiator instantiator) {
        this.instantiator = instantiator
    }

    def provisioner(Map<String, Class<Provisioner>> provisionerClasses) {
        provisionerClasses.entrySet().each { registerProvisioner(it.key, it.value) }
    }

    def hosting(Map<String, Class<Hosting>> hostingClasses) {
        hosting(hostingClasses, null)
    }

    def hosting(Map<String, Class<Hosting>> hostingClasses, Closure closure) {
        hostingClasses.entrySet().each {
            if (hostings.containsKey(it.key)) throw IllegalArgumentException("duplicate hosting: $it.key")
            def factory = new HostingFactory(it.value)
            if (closure != null) ConfigureUtil.configure(closure, factory)
            hostings.put(it.key, factory)
        }
    }

    def application(Closure closure) {
        ConfigureUtil.configure(closure, new ApplicationFacetHandler(this))
    }

    class HostingFactory {
        Class<Hosting> hostingClass
        Map<String, Class<Hosting.Feature>> features = new HashMap<>()

        HostingFactory(Class<Hosting> hostingClass) {
            this.hostingClass = hostingClass
        }

        def feature(Map<String, Class<Hosting.Feature>> featureClasses) {
            featureClasses.entrySet().each {
                if (features.containsKey(it.key)) throw IllegalArgumentException("duplicate hosting: $it.key")
                features.put(it.key, it.value)
            }
        }

        def create() {
            def hosting = instantiator.newInstance(hostingClass)
            hosting.extensions.add("features", [])
            features.entrySet().each { registered ->
                hosting.metaClass."$registered.key" = { Closure closure ->
                    def feature = instantiator.newInstance(registered.value)
                    ConfigureUtil.configure(closure, feature)
                    hosting.features.add(feature)
                }
            }
            return hosting
        }
    }

    private void registerProvisioner(String name, Class<Provisioner> provisionerClass) {
        def provisioner = instantiator.newInstance(provisionerClass)
        if (provisioners.containsKey(name)) throw IllegalArgumentException("duplicate provisioner: $name")
        provisioners.put(name, provisioner)
    }
}
