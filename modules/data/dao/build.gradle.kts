plugins {
    id("org.jetbrains.kotlin.jvm")
    id("ru.astrainteractive.gradleplugin.java.version")
    alias(libs.plugins.klibs.rules.gradle.linter)
    `java-test-fixtures`
}

configurations.api {
    dependencies.removeIf { dependency ->
        dependency.group == "org.jetbrains.kotlin" && dependency.name == "kotlin-stdlib"
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.minecraft.astralibs.core)

    testFixturesImplementation(libs.minecraft.astralibs.core)

    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.minecraft.astralibs.core)
    testImplementation(libs.tests.kotlin.test)
}
