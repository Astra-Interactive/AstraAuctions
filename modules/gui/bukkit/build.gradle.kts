plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(libs.plugins.klibs.rules.gradle.linter)
    id("ru.astrainteractive.gradleplugin.java.version")
}

dependencies {
    implementation(libs.klibs.kstorage)
    compileOnly(libs.minecraft.paper.api)
    implementation(libs.klibs.mikro.core)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    implementation(projects.modules.data.dao)
    implementation(projects.modules.core)
    implementation(projects.modules.coreBukkit)
    implementation(projects.modules.gui.api)
}

linter {
    unitKind.set("technology")
}
