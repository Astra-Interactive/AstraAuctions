plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(libs.minecraft.paper.api)

    implementation(libs.klibs.mikro.core)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.astralibs.menu.bukkit)

    implementation(projects.modules.apiMarket)
    implementation(projects.modules.core)
    implementation(projects.modules.coreBukkit)
    implementation(projects.modules.gui.api)
}
