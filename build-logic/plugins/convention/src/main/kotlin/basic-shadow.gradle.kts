import org.gradle.ide.visualstudio.tasks.internal.RelativeFileNameTransformer
import org.gradle.kotlin.dsl.dependencies

plugins {
    java
    `java-library`
    id("org.jetbrains.kotlin.jvm")
    id("com.github.johnrengelman.shadow")
}
tasks.shadowJar {
    isReproducibleFileOrder = true
    mergeServiceFiles()
    dependsOn(configurations)
    archiveClassifier.set(null as String?)
    from(sourceSets.main.get().output)
    from(project.configurations.runtimeClasspath)
    relocate("org.bstats", "${libs.versions.group.get()}.astramarket")
    minimize()
    archiveBaseName.set(libs.versions.name.get())
    destinationDirectory.set(File(libs.versions.destinationDirectoryPath.get()))
}
