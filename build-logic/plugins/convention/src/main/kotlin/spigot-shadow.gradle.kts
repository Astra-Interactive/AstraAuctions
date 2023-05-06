plugins {
    java
    `java-library`
    id("org.jetbrains.kotlin.jvm")
    id("com.github.johnrengelman.shadow")
}

tasks.shadowJar {
    dependsOn(tasks.processResources)

    isReproducibleFileOrder = true
    mergeServiceFiles()
    relocate("org.bstats", libs.versions.project.group.get())
    listOf(
        "kotlin",
        "org.jetbrains",
        libs.minecraft.astralibs.ktxcore.get().module.group
    ).forEach {
        relocate(it, libs.versions.project.group.get() + ".$it")
    }
    dependsOn(configurations)
    archiveClassifier.set(null as String?)
    from(sourceSets.main.get().output)
    from(project.configurations.runtimeClasspath)
    minimize()
    archiveBaseName.set(libs.versions.project.name.get())
    File(libs.versions.destination.paper.get()).let {
        if (!it.exists()) {
            File(rootDir, "jars").also(File::mkdirs)
        } else {
            it
        }
    }.also(destinationDirectory::set)
}
