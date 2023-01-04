package astralibs


import org.gradle.api.Project
import java.io.InputStream
import java.util.Properties

object GradleALibrary {
//    val GROUP = libs.versions.group.get()
//    val VERSION = libs.versions.plugin.get()
//    val DESCRIPTION = libs.versions.description.get()
    val Project.KEY_ALIAS: String
        get() = getCredential(this, "KEY_ALIAS")

    fun getCredential(project: Project, path: String): String {
        val properties: Properties = Properties()
        val inputStream: InputStream = project.rootProject.file("keys.properties").inputStream()
        properties.load(inputStream)
        return properties.getProperty(path)
    }
}