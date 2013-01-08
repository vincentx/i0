package com.thoughtworks.i0.gradle.facets

import com.thoughtworks.i0.gradle.core.ApplicationFacet
import com.thoughtworks.i0.gradle.core.Environment
import com.thoughtworks.i0.gradle.core.EnvironmentSet
import com.thoughtworks.i0.gradle.core.Provisioner
import com.thoughtworks.i0.gradle.facets.puppet.Module
import com.thoughtworks.i0.gradle.facets.puppet.ModuleServerSet
import com.thoughtworks.i0.gradle.facets.puppet.ModuleSet
import org.gradle.api.Project

import java.nio.file.Files

class Puppet implements Provisioner {
    ModuleServerSet servers = new ModuleServerSet()

    @Override
    void configure(EnvironmentSet environments) {
        environments.extensions.add("moduleServers", servers)
    }

    @Override
    void configure(Environment environment) {
        environment.extensions.create("modules", ModuleSet)
    }

    @Override
    boolean configure(ApplicationFacet facet, Environment environment) {
        try {
            facet.puppet(environment.modules)
            return true
        } catch (MissingMethodException e) {
            return false
        }
    }

    @Override
    void resolve(Project project, Environment environment, File root) {
        def temporaryDir = Files.createTempDirectory("puppet_module")
        for (Module module in environment.modules) {
            def file = project.environments.moduleServers.inject(null) { r, i ->
                r != null ? r : i.download(project.ant(), module, temporaryDir)
            }
            if (file == null) throw new RuntimeException("$module.user/$module.name could not be downloaded")
            project.copy {
                from(project.tarTree("$temporaryDir/$file")) {
                    eachFile { details ->
                        details.path =
                            details.path.substring(details.relativePath.segments[0].length())
                    }
                }
                into new File(root, "modules/$module.name")
            }
            project.delete new File(root, "modules/$module.name/$file")
        }
    }

    @Override
    void generateScaffold(Project project) {
        project.mkdir(project.file("src/puppet/modules"))
        project.mkdir(project.file("src/puppet/manifests"))

        for (environment in project.environments) {
            project.file("src/puppet/manifests/${environment.name}.pp").withWriter {
                it.write(
                        """
                        |node default {
                        |${environment.modules.sort { it.priority }.collect { "   $it.configuration" }.join("\n") }
                        |}
                        """.stripMargin().toString()
                )
            }
        }

    }

    def vagrant(Project project, Environment environment, File root, Vagrant v) {
        v.puppetSolo(["modules", relative(project, root, "src/puppet/modules")], relative(project, root, "src/puppet/manifests"), environment.name)
    }

    private def relative(Project project, File root, String path) {
        return root.toURI().relativize(project.file(path).toURI()).getPath()
    }
}
