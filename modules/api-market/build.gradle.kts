plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}
dependencies {
    // Kotlin
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.serialization.kaml)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)

    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.klibs.mikro.extensions)
    implementation(libs.klibs.mikro.core)
    // Test

    testImplementation(libs.tests.kotlin.test)
    testImplementation("com.h2database:h2:2.4.240")
    // Local
    implementation(projects.modules.core)
}
