package com.thoughtworks.i0.plugins.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.jar.JarEntry

class InitTask extends DefaultTask {
    @TaskAction
    def init() {
        def dirs = ['src/main/java', 'src/main/resources', 'src/main/resources/META-INF', 'src/test/java', 'src/test/resources',
                'src/main/puppet', 'lib/modules']
        dirs.each { new File(project.projectDir, it).mkdirs() }

        copyJar(project.projectDir, (JarURLConnection) InitTask.getResource("/scaffolding/init").openConnection())
    }

    def copy(InputStream input, File target) {
        def reader = input.newReader()
        target.withWriter { writer -> writer << reader }
        reader.close()
    }

    def copyJar(File destination, JarURLConnection jarConnection) {
        def jarFile = jarConnection.jarFile
        for (JarEntry entry in jarFile.entries()) {
            if (entry.name.startsWith(jarConnection.entryName)) {
                def fileName = entry.name.substring(jarConnection.entryName.length())
                if (!entry.isDirectory()) {
                    copy(jarFile.getInputStream(entry), new File(destination, fileName))
                } else {
                    new File(destination, fileName).mkdirs()
                }
            }
        }
    }
}
