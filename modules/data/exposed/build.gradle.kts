plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(libs.plugins.klibs.gradle.linter)
    id("ru.astrainteractive.gradleplugin.java.version")
}
dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.klibs.kstorage)
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.mikro.extensions)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.serialization.kaml)
    implementation(libs.minecraft.astralibs.core)
    implementation(projects.modules.data.dao)
    testImplementation(libs.driver.h2)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.tests.kotlin.test)
}
