package com.thoughtworks.i0.gradle.deploy

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project

class Vagrant {
    static def VAGRANT_FILE_TEMPLATE =
        '''
           |# -*- mode: ruby -*-
           |# vi: set ft=ruby :
           |
           |Vagrant::Config.run do |config|
           |    config.vm.box = "$box"
           |    config.vm.box_url = "$boxUrl"
           |
           |    $forwardPorts
           |
           |    $shareFolders
           |
           |    $provision
           |end
           '''.stripMargin()

    static def PUPPET_PROVISION_TEMPLATE =
        '''
           |    config.vm.provision :puppet do |puppet|
           |        puppet.module_path = ["vendor/puppet"]
           |        puppet.manifests_path = "src/main/puppet/manifests"
           |        puppet.manifest_file  = "development.pp"
           |    end
            '''.stripMargin()

    private static def engine = new SimpleTemplateEngine()

    static def vagrantFile(Project project, vagrant) {
        def provision = ''
        if (project.extensions.findByName("puppet") != null)
            provision = engine.createTemplate(PUPPET_PROVISION_TEMPLATE).make([:])

        def shareFolders = vagrant.shareFolders.size > 0 ? "config.vm.share_folder ${vagrant.shareFolders.collect { "\"it\"" }.join(",")}" : ""

        def forwardPorts = vagrant.ports.size > 0 ? vagrant.ports.collect { "config.vm.forward_port $it" }.join("\n") : ""

        return engine.createTemplate(VAGRANT_FILE_TEMPLATE).make(
                ["box": vagrant.box, "boxUrl": vagrant.boxUrl,
                        "forwardPorts": forwardPorts, "shareFolders": shareFolders,
                        "provision": provision
                ])
    }
}
