package com.thoughtworks.i0.plugins.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class InitTask extends DefaultTask {
    @TaskAction
    def init() {
        def dirs = ['src/main/java', 'src/main/resources', 'src/main/resources/META-INF', 'src/test/java', 'src/test/resources']
        dirs.each { new File(project.projectDir, it).mkdirs() }

        copy("/scaffolding/init/persistence.xml", new File(project.projectDir, 'src/main/resources/META-INF/persistence.xml'))
        copy("/scaffolding/init/your_app.yml", new File(project.projectDir, 'your_app.yml'))
    }

    def copy(String file, File target) {
        def reader = InitTask.getResource(file).newReader()
        target.withWriter { writer ->
            writer << reader
        }
        reader.close()
    }
}
