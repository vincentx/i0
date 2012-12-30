package com.thoughtworks.i0.gradle.facets

import com.thoughtworks.i0.gradle.core.Environment
import com.thoughtworks.i0.gradle.core.Hosting
import org.gradle.api.Project

class Vagrant implements Hosting {
    Map<Integer, Integer> ports = [:]
    Map<String, Hosting.Feature> facets = [:]
    String box = "precise32"
    String boxUrl = "http://files.vagrantup.com/precise32.box"
    String provision = ""

    def forward(int from, int to) {
        ports[from] = to
    }

    def puppetSolo(modules, manifests, manifest) {
        provision =
            """
           |    config.vm.provision :puppet do |puppet|
           |        puppet.module_path = [${modules.collect { "\"$it\"" }.join(",")}]
           |        puppet.manifests_path = "$manifests"
           |        puppet.manifest_file  = "${manifest}.pp"
           |    end""".stripMargin()
    }

    @Override
    void environment(Project project, Environment environment, File root) {
        project.provisioner.resolve(project, environment, root)
        try {
            project.provisioner.vagrant(project, environment, root, this)
        } catch (MissingMethodException e) {
        }
        vagrantfile(root)
    }

    private def vagrantfile(File root) {
        new File(root, "Vagrantfile").withWriter {
            it.write(
                    """
           |# -*- mode: ruby -*-
           |# vi: set ft=ruby :
           |
           |Vagrant::Config.run do |config|
           |    config.vm.box = "$box"
           |    config.vm.box_url = "$boxUrl"
           |
           |    $provision
           |end""".stripMargin().toString())
        }
    }
}
