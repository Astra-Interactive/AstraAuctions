import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.named
import ru.astrainteractive.gradleplugin.property.extension.ModelPropertyValueExt.requireProjectInfo

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.gradle.shadow)
    alias(libs.plugins.klibs.minecraft.resource.processor)
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization.json)

    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.klibs.mikro.extensions)
    implementation(libs.klibs.mikro.core)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.command.bukkit)
    // Test
    testImplementation(libs.tests.kotlin.test)
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
    implementation(libs.minecraft.bstats)
    compileOnly(libs.minecraft.papi)
    compileOnly(libs.minecraft.vaultapi)
    implementation(libs.minecraft.bstats)
    // Local
    implementation(projects.modules.apiMarket)
    implementation(projects.modules.core)
    implementation(projects.modules.coreBukkit)
    implementation(projects.modules.periodic)
    implementation(projects.modules.gui.players.api)
    implementation(projects.modules.gui.slots.api)
    implementation(projects.modules.gui.slots.bukkit)
    implementation(projects.modules.gui.commonBukkit)
    implementation(projects.modules.commandBukkit)
}

minecraftProcessResource {
    bukkit()
}

val shadowJar = tasks.named<ShadowJar>("shadowJar")
shadowJar.configure {
    mergeServiceFiles()
    dependsOn(tasks.named<ProcessResources>("processResources"))
    isReproducibleFileOrder = true
    archiveClassifier = null as String?
    archiveVersion.set(requireProjectInfo.versionString)
    archiveBaseName.set("${requireProjectInfo.name}-bukkit")
    destinationDirectory = rootProject
        .layout.buildDirectory.asFile.get()
        .resolve("bukkit")
        .resolve("plugins")
        .takeIf(File::exists)
        ?: rootDir.resolve("jars").also(File::mkdirs)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    configurations = listOf(project.configurations.runtimeClasspath.get())
    relocationPrefix = requireProjectInfo.group
//    enableRelocation = true
    minimize {
        exclude(dependency(libs.exposed.jdbc.get()))
        exclude(dependency(libs.exposed.dao.get()))
        exclude(dependency(libs.exposed.core.get()))
    }
}
