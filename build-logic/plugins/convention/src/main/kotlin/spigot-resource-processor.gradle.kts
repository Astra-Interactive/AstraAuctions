import org.gradle.api.file.DuplicatesStrategy

plugins {
    java
    `java-library`
    id("org.jetbrains.kotlin.jvm")
}

tasks.processResources {
    filteringCharset = "UTF-8"
    from(sourceSets.main.get().resources.srcDirs) {
        // It is available to declare dependencies like this
        val implementations: List<String> = listOf(
            libs.kotlin.coroutines.core,
            libs.kotlin.coroutines.coreJvm,
            libs.kotlin.serialization,
            libs.kotlin.serializationJson,
            libs.kotlin.serializationKaml,
            libs.kotlin.gradle,
        ).map { it.get().toString() }
        filesMatching("plugin.yml") {
            expand(
                "main" to "${libs.versions.project.group.get()}.${libs.versions.project.name.get()}",
                "name" to libs.versions.project.name.get(),
                "prefix" to libs.versions.project.name.get(),
                "version" to libs.versions.project.version.get(),
                "description" to libs.versions.project.description.get(),
                "url" to libs.versions.project.url.get(),
                "author" to libs.versions.project.author.get(),
                "authors" to libs.versions.project.authors.get().split(";").joinToString("\",\""),
                "libraries" to implementations.joinToString("\",\""),
            )
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}
