
import ru.astrainteractive.gradleplugin.setupSpigotProcessor
import ru.astrainteractive.gradleplugin.setupSpigotShadow

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // AstraLibs
    implementation(libs.minecraft.astralibs.ktxcore)
    implementation(libs.minecraft.astralibs.orm)
    implementation(libs.klibs.kdi)
    implementation(libs.minecraft.astralibs.spigot.gui)
    implementation(libs.minecraft.astralibs.spigot.core)
    implementation(project(mapOf("path" to ":shared")))
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
    implementation(libs.minecraft.bstats)
    compileOnly(libs.minecraft.papi)
    compileOnly(libs.minecraft.vaultapi)
    implementation(libs.minecraft.bstats)
    implementation(projects.data)
    implementation(projects.shared)

    implementation(projects.shared.dependencyProject.sourceSets.getByName("bukkitMain").output)
}

File("D:\\Minecraft Servers\\Servers\\esmp-configuration\\anarchy\\plugins").let { file ->
    if (file.exists()) setupSpigotShadow(file) else setupSpigotShadow()
}

setupSpigotProcessor()
