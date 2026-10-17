
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(libs.plugins.klibs.gradle.linter)
    id("ru.astrainteractive.gradleplugin.java.version")
}

dependencies {
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.mikro.extensions)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.minecraft.astralibs.core)
    implementation(projects.modules.data.dao)
    implementation(projects.modules.core)
}
