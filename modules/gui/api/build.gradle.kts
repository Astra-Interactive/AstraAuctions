
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(libs.plugins.klibs.rules.gradle.linter)
    id("ru.astrainteractive.gradleplugin.java.version")
}

dependencies {
    implementation(libs.klibs.kstorage)
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.mikro.extensions)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.minecraft.astralibs.core)
    implementation(projects.modules.data.dao)
    implementation(projects.modules.core)

    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.minecraft.kyori.api)
    testImplementation(libs.tests.kotlin.test)
    testImplementation(testFixtures(projects.modules.core))
    testImplementation(testFixtures(projects.modules.data.dao))
}
