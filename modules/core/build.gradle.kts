plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("ru.astrainteractive.gradleplugin.detekt")
    id("ru.astrainteractive.gradleplugin.java.version")
}

dependencies {
    compileOnly(libs.minecraft.kyori.minimessage)

    api(libs.klibs.kstorage)
    implementation(libs.klibs.mikro.core)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.serialization.kaml)
    implementation(libs.minecraft.astralibs.core)

    testImplementation(libs.minecraft.kyori.legacy)
    testImplementation(libs.minecraft.kyori.minimessage)
    testImplementation(libs.minecraft.kyori.plain)
    testImplementation(libs.tests.kotlin.test)
}
