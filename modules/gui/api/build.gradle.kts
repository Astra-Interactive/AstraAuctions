
plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.mikro.extensions)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.minecraft.astralibs.core)

    implementation(projects.modules.apiMarket)
    implementation(projects.modules.core)
}
