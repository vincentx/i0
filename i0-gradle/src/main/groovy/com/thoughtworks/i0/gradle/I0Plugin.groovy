package com.thoughtworks.i0.gradle

import com.thoughtworks.i0.gradle.core.I0BasePlugin
import com.thoughtworks.i0.gradle.facets.Java
import com.thoughtworks.i0.gradle.facets.Puppet
import com.thoughtworks.i0.gradle.facets.Vagrant
import com.thoughtworks.i0.gradle.facets.frameworks.HibernateJpa
import com.thoughtworks.i0.gradle.facets.frameworks.I0
import com.thoughtworks.i0.gradle.facets.frameworks.Jersey
import com.thoughtworks.i0.gradle.facets.frameworks.Jetty
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.util.ConfigureUtil

class I0Plugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply(JavaPlugin)
        project.plugins.apply(I0BasePlugin)

        configure(project) {
            facets {
                application {
                    language java: Java
                    embedded jetty: Jetty
                    restful jersey: Jersey
                    persistence {
                        jpa hibernate: HibernateJpa
                    }
                    framework i0: I0
                }

                provisioner puppet: Puppet
                hosting vagrant: Vagrant
            }

            application {
                framework {
                    i0 {
                        version = "0.2.0"
                        guice = "3.0"
                        slf4j = "1.7.2"
                        logback = "1.0.7"
                        jackson = "2.1.1"
                        hibernateValdator = "4.3.0.Final"
                    }
                }
            }
        }
    }

    private def configure(Project project, Closure closure) {
        ConfigureUtil.configure(closure, project)
    }
}
