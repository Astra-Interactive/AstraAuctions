plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.klibs.mikro.core)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
    // Local
    implementation(projects.modules.apiMarket)
    implementation(projects.modules.core)
    implementation(projects.modules.coreBukkit)
    implementation(projects.modules.gui.players.api)
    implementation(projects.modules.gui.slots.api)
}
