plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
    id("basic-plugin")
    id("basic-shadow")
    id("basic-resource-processor")
}

dependencies {
    // Kotlin
    implementation(libs.kotlinGradlePlugin)
    // Coroutines
    implementation(libs.coroutines.coreJvm)
    implementation(libs.coroutines.core)
    // Serialization
    implementation(libs.kotlin.serialization)
    implementation(libs.kotlin.serializationJson)
    implementation(libs.kotlin.serializationKaml)
    // AstraLibs
    implementation(libs.astralibs.ktxCore)
    implementation(libs.astralibs.spigotCore)
    // Test-Core
    testImplementation(kotlin("test-junit5"))
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // Spigot dependencies
    compileOnly(libs.essentialsx)
    compileOnly(libs.paperApi)
    compileOnly(libs.spigotApi)
    compileOnly(libs.spigot)
    compileOnly(libs.protocollib)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.worldguard.bukkit)
    compileOnly(libs.discordsrv)
    compileOnly(libs.vaultapi)
    compileOnly(libs.coreprotect)
    implementation(libs.bstats.bukkit)
    implementation(project(":domain"))
}