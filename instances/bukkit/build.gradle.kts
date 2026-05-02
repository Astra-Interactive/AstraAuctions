import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.named
import ru.astrainteractive.gradleplugin.property.util.requireProjectInfo

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.gradle.shadow)
    alias(libs.plugins.klibs.minecraft.resource.processor)
}

dependencies {
    compileOnly(libs.minecraft.paper.api)
    compileOnly(libs.minecraft.papi)
    compileOnly(libs.minecraft.vaultapi)

    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.mikro.extensions)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.command.bukkit)
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    implementation(libs.minecraft.bstats)
    implementation(libs.minecraft.bstats)

    implementation(projects.modules.apiMarket)
    implementation(projects.modules.commandBukkit)
    implementation(projects.modules.core)
    implementation(projects.modules.coreBukkit)
    implementation(projects.modules.gui.api)
    implementation(projects.modules.gui.bukkit)
    implementation(projects.modules.service)

    testImplementation(libs.tests.kotlin.test)
}

minecraftProcessResource {
    bukkit(
        customProperties = mapOf(
            "libraries" to listOf(
                libs.driver.h2.get(),
                libs.driver.jdbc.get(),
                libs.driver.mysql.get(),
                libs.driver.mariadb.get()
            ).joinToString("\",\"", "[\"", "\"]")
        )
    )
}
val shadowJar = tasks.named<ShadowJar>("shadowJar")
shadowJar.configure {

    val projectInfo = requireProjectInfo
    isReproducibleFileOrder = true
    mergeServiceFiles()
    dependsOn(configurations)
    archiveClassifier.set(null as String?)

//    minimize {
//        exclude(dependency(libs.exposed.jdbc.get()))
//        exclude(dependency(libs.exposed.dao.get()))
//    }
    archiveVersion.set(projectInfo.versionString)
    archiveBaseName = "${requireProjectInfo.name}-${project.name}"
    destinationDirectory = rootDir.resolve("build")
        .resolve("bukkit")
        .resolve("plugins")
        .takeIf(File::exists)
        ?: File(rootDir, "jars").also(File::mkdirs)

    dependencies {
        // Dependencies
        exclude("mozilla/**")
        exclude("javax/**")
        exclude("it/unimi/dsi/**")
        exclude("ch/qos/logback/**")
        exclude("org/intellij/lang/annotations/**")
        exclude("org/jetbrains/annotations/**")
        exclude("org/slf4j/**")
        exclude("org/apache/xmlgraphics/**")
        exclude("org/apache/batik/**")
        exclude("org/apache/commons/logging/**")
        exclude("com/ibm/icu/**")
        // Root
        exclude("_COROUTINE/**")
        exclude("DebugProbesKt.bin")
        exclude("jetty-dir.css")
        exclude("license/**")
        exclude("licenses/**")
        exclude("**LICENCE**")
        exclude("**LICENSE**")
        // META
        exclude("META-INF/**.md")
        exclude("META-INF/**.MD")
        exclude("META-INF/**.txt**")
        exclude("META-INF/**LICENCE**")
        exclude("META-INF/com.android.tools/**")
        exclude("META-INF/gradle-plugins/**")
        exclude("META-INF/imports/**")
        exclude("META-INF/kotlin-reflection.kotlin_module")
        exclude("META-INF/license/**")
        exclude("META-INF/maven/**")
        exclude("META-INF/native-image/**")
        exclude("META-INF/native/**")
        exclude("META-INF/proguard/**")
        exclude("META-INF/rewrite/**")
        exclude("META-INF/services/kotlin.reflect.**")
        exclude("META-INF/versions/**")
        exclude(dependency("mysql:mysql-connector-java"))
        exclude(dependency("com.mysql:mysql-connector-j"))
        exclude(dependency("org.xerial:sqlite-jdbc"))
        exclude(dependency("com.mojang:brigadier"))
        exclude(dependency("net.kyori:.*"))
    }
    relocate("org.bstats", projectInfo.group)
    listOf(
        "ch.qos.logback",
        "com.charleskorn.kaml",
        "com.ibm.icu",
        "it.krzeminski.snakeyaml",
        "net.thauvin.erik",
        "okio",
        "org.apache",
        "org.intellij",
        "org.jetbrains.annotations",
        "ru.astrainteractive.klibs",
        "ru.astrainteractive.astralibs",
        "io.github.reactivecircus",
        "co.touchlab.stately",
        "google.protobuf",
    ).forEach { pattern -> relocate(pattern, "${projectInfo.group}.$pattern") }
    listOf(
        "kotlinx",
    ).forEach { pattern ->
        relocate(pattern, "${projectInfo.group}.$pattern") {
            exclude("kotlin/kotlin.kotlin_builtins")
        }
    }
}
