package ru.astrainteractive.buildlogic

import libs
import org.gradle.api.Task
import java.io.File

class PublishPrimaryVersionTask(private val task: Task) {
    operator fun invoke() {
        val libs = task.project.libs
        val rootDir = task.project.rootDir

        val file = File(File(rootDir, ".github"), "version.env")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        file.writeText("")
        mapOf(
            "MAJOR_VERSION" to libs.versions.project.version.get(),
            "PROJECT_NAME" to libs.versions.project.name.get()
        ).forEach {
            file.appendText(
                "${it.key}=${it.value}\n"
            )
        }
    }
}
