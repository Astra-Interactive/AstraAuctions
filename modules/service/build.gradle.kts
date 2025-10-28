plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.coroutines.core)

    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.klibs.mikro.core)

    testImplementation(libs.tests.kotlin.test)

    // Local
    implementation(projects.modules.apiMarket)
    implementation(projects.modules.core)
}
