plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(libs.minecraft.paper.api)
    compileOnly(libs.minecraft.vaultapi)

    implementation(libs.klibs.mikro.core)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.serialization.kaml)
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    implementation(libs.minecraft.bstats)

    implementation(projects.modules.core)
}
