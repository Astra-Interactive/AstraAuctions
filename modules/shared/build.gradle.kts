// import ru.astrainteractive.gradleplugin.sourceset.JvmSourceSet.Companion.configureAstraSourceSet

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("ru.astrainteractive.gradleplugin.minecraft.multiplatform")
}

minecraftMultiplatform {
    bukkit()
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // AstraLibs
    implementation(libs.minecraft.astralibs.ktxcore)
    implementation(libs.minecraft.astralibs.spigot.core)
    implementation(libs.minecraft.astralibs.spigot.gui)
    implementation(libs.minecraft.astralibs.orm)
    implementation(libs.klibs.kdi)
    implementation(libs.minecraft.bstats)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    // Bukkit
    "bukkitMainCompileOnly"(libs.minecraft.paper.api)
    // Local
    implementation(projects.modules.apiMarket)
    implementation(projects.modules.core)
}
