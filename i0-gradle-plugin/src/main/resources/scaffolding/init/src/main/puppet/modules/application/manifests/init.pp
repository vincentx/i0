class application {
  file { "config.yml":
      path => "/opt/app/config.yml",
      ensure  => present,
      mode    => 0644,
      source  => "puppet:///modules/application/development.yml"
  }
  exec { "start-server":
      command => "/usr/bin/java -jar deploy.jar &",
      cwd => "/opt/app"
  }
}