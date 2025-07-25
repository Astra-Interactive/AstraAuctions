import ru.astrainteractive.gradleplugin.property.extension.ModelPropertyValueExt.requireProjectInfo

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.minecraft.shadow)
    alias(libs.plugins.klibs.minecraft.resource.processor)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.exposed)
    implementation(libs.klibs.mikro.core)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.command.bukkit)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
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

astraShadowJar {
    requireShadowJarTask {
        destination = rootDir
            .resolve("build")
            .resolve("bukkit")
            .resolve("plugins")
            .takeIf { it.exists() }
            ?: File(rootDir, "jars")

        val projectInfo = requireProjectInfo
        isReproducibleFileOrder = true
        mergeServiceFiles()
        dependsOn(configurations)
        archiveClassifier.set(null as String?)
        listOf(
            "org.bstats",
            "kotlin",
            "org.jetbrains",
            "ru.astrainteractive.astralibs"
        ).forEach { pattern -> relocate(pattern, "${projectInfo.group}.$pattern") }

        minimize {
            exclude(dependency(libs.exposed.jdbc.get()))
            exclude(dependency(libs.exposed.dao.get()))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.version.get()}"))
        }
        archiveVersion.set(projectInfo.versionString)
        archiveBaseName.set("${projectInfo.name}-bukkit")
        destinationDirectory.set(destination.get())
    }
}
