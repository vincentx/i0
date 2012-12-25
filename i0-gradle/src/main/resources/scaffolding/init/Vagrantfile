# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant::Config.run do |config|
  config.vm.box = "precise32"
  config.vm.box_url = "http://files.vagrantup.com/precise32.box"

  config.vm.forward_port 8080, 8001

  config.vm.share_folder "application", "/opt/app", "./build/libs"

  config.vm.provision :puppet do |puppet|
     puppet.module_path = ["lib/modules", "src/main/puppet/modules"]
     puppet.manifests_path = "src/main/puppet/manifests"
     puppet.manifest_file  = "development.pp"
  end
end
