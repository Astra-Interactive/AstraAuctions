plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}
dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.mikro.extensions)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.serialization.kaml)
    implementation(libs.minecraft.astralibs.core)

    implementation(projects.modules.core)

    testImplementation(libs.driver.h2)
    testImplementation(libs.tests.kotlin.test)
}
