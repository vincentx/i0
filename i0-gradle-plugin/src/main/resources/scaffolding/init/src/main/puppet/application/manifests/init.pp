class apt {
  exec { "apt-update":
    command => "/usr/bin/apt-get update"
  }

  Apt::Key <| |> -> Exec["apt-update"]
  Apt::Source <| |> -> Exec["apt-update"]

  Exec["apt-update"] -> Package <| |>
}

class application {
  file { "config.yml":
      path => "/opt/app",
      ensure  => present,
      mode    => 0644,
      source  => "puppet:///modules/application/development.yml"
  }
  exec { "start-server":
      command => "",  # /usr/bin/java -jar your-deploy.jar appname &,
      cwd => "/opt/app"
  }
}