plugins {
    id("org.jetbrains.kotlin.jvm")
    id("ru.astrainteractive.gradleplugin.java.version")
    alias(libs.plugins.klibs.gradle.linter)
    `java-test-fixtures`
}

dependencies {
    implementation(libs.minecraft.astralibs.core)

    testFixturesImplementation(libs.minecraft.astralibs.core)

    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.minecraft.astralibs.core)
    testImplementation(libs.tests.kotlin.test)
}
