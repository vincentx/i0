package com.thoughtworks.i0.gradle

import com.thoughtworks.i0.gradle.deploy.Module
import com.thoughtworks.i0.gradle.deploy.ModuleServers
import com.thoughtworks.i0.gradle.deploy.Modules
import com.thoughtworks.i0.gradle.deploy.Vagrant
import org.gradle.api.Project

import static com.thoughtworks.i0.gradle.Configuration.nullable
import static com.thoughtworks.i0.gradle.deploy.Puppet.initDevelopmentManifest

class Deploy {
    static Component provision = new Component("deploy", "provision").extend("puppet", nullable(
            modules: [],
            classes: []
    ), Deploy.&puppet)

    static Component localEnvironment = new Component(["deploy", "environment"], "local").extend("vagrant", nullable(
            box: "precise32",
            boxUrl: "http://files.vagrantup.com/precise32.box",
            ports: [],
            shareFolders: []
    ), Deploy.&vagrant)


    static def puppet(Project project, puppet) {
        project.extensions.create("puppetModuleServers", ModuleServers)
        project.extensions.create("puppet", Modules)
        project.extensions.puppetClasses = new HashSet<String>()

        project.puppetModuleServers {
            puppetForge()
        }

        project.task('resolvePuppetModules') << {
            project.delete "vendor/puppet"
            def allModules = []
            allModules.addAll(project.extensions.puppet.modules)
            allModules.addAll(puppet.modules.collect { Module.of(it) })
            for (module in allModules.toSet()) {
                def file = project.extensions.puppetModuleServers.servers.inject(null) { r, i -> r == null ? i.download(project.ant, module, temporaryDir) : r }
                if (file == null) throw new RuntimeException("$module.user/$module.module could not be downloaded")
                project.copy {
                    from(project.tarTree("$temporaryDir/$file")) {
                        eachFile { details ->
                            details.path =
                                details.path.substring(details.relativePath.segments[0].length())
                        }
                    }
                    into "vendor/puppet/$module.name"
                }
                project.delete "vendor/puppet/$module.name/$file"
            }
        }

        project.task('initPuppet', dependsOn: 'resolvePuppetModules') << {
            initDevelopmentManifest(project)

        }

    }

    static def vagrant(Project project, vagrant) {
        project.task("initVagrant") << {
            new File('Vagrant').withWriter { it.write(Vagrant.vagrantFile(project, vagrant)) }
        }
    }
}
