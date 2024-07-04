# Load the contents of environment.properties into a Bash variable
environment = {}
File.readlines('environment.properties').each do |line|
  next if line.nil? || line.empty? || line.start_with?("#")

  line = line.strip
  key, value = line.split('=')
  next if key.nil? || value.nil?

  environment[key.strip] = value.strip
end

Vagrant.configure("2") do |config|

  config.vm.synced_folder environment["mount_path"], "/vagrant" 
  config.vm.boot_timeout = 1000
  config.vm.box = "bento-ubuntu-22-04.box"
  config.vm.box_url = "file://./boxes/vbox-ubuntu/bento-ubuntu-22-04.box"
  config.ssh.private_key_path = ["./boxes/vbox-ubuntu/private_key"]

  config.vm.provision "shell", inline: <<-SHELL
    sudo apt update -y
    sudo apt install dos2unix -y
  SHELL

  config.vm.define "postgresql" do |postgresql|
    postgresql.vm.define "postgresql"
    postgresql.vm.hostname = "postgresql"
    postgresql.vm.network "private_network", ip: "10.0.0.10"
    postgresql.vm.network "forwarded_port", guest: 22,   host: 10022, host_ip: "0.0.0.0", id: "ssh"

    postgresql.vm.provider "virtualbox" do |vm| 
      vm.name = "postgres"
      vm.gui = false
      vm.cpus = 2
      vm.memory = "8096"
      vm.customize ["modifyvm", :id, "--groups", ("/" + environment["cluster_name"])]
    end
    postgresql.vm.provision "shell", privileged: false, name: "Dos2Unix", env: environment, path: "dos2unix.sh"
	  postgresql.vm.provision "shell", privileged: false, name: "Dos2Unix", env: environment, path: "provision/postgres/install/ubuntu/postgresql_vm_provision.sh"
  end

  config.vm.define "keycloak" do |keycloak|
    keycloak.vm.define "keycloak"
    keycloak.vm.hostname = "keycloak"
    keycloak.vm.network "private_network", ip: "10.0.0.15"
    keycloak.vm.network "forwarded_port", guest: 22,   host: 15022, host_ip: "0.0.0.0", id: "ssh"

    keycloak.vm.provider "virtualbox" do |vm| 
      vm.name = "keycloak"
      vm.gui = false
      vm.cpus = 2
      vm.memory = "8096"
      vm.customize ["modifyvm", :id, "--groups", ("/" + environment["cluster_name"])]
    end
    keycloak.vm.provision "shell", privileged: false, name: "Dos2Unix", env: environment, path: "dos2unix.sh"
	  keycloak.vm.provision "shell", privileged: false, name: "Dos2Unix", env: environment, path: "provision/keycloak/keycloak_provision.sh"
  end
end