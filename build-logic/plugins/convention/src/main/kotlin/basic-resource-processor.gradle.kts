import org.gradle.api.file.DuplicatesStrategy
import org.gradle.ide.visualstudio.tasks.internal.RelativeFileNameTransformer
import org.gradle.kotlin.dsl.dependencies

plugins {
    java
    `java-library`
    id("org.jetbrains.kotlin.jvm")
}

tasks.processResources {
    filteringCharset = "UTF-8"
    from(sourceSets.main.get().resources.srcDirs) {
        filesMatching("plugin.yml") {
            expand(
                "name" to libs.versions.name.get(),
                "version" to libs.versions.plugin.get(),
                "description" to libs.versions.description.get()
            )
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}
