plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
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
}
