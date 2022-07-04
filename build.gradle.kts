val kotlin_version: String by project
val kotlin_coroutines_version: String by project
val kotlin_json_version: String by project
val kaml: String by project

group = "com.astrainteractive"
version = "1.1.0"
val name = "AstraMarket"
description = "Global Market plugin from AstraInteractive for EmpireSMP"

plugins {
    java
    `maven-publish`
    `java-library`
    kotlin("jvm") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}
java {
    withSourcesJar()
    withJavadocJar()
    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_17
}
repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://nexus.scarsz.me/content/groups/public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.essentialsx.net/snapshots/")
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo1.maven.org/maven2/")
    maven("https://maven.playpro.com")
    maven("https://jitpack.io")
    flatDir { dirs("libs") }
}

dependencies {
    val spigot = "1.19-R0.1-SNAPSHOT"
    val vault = "1.7"
    val placeholderapi = "2.11.1"
    val protocolLib = "4.8.0"
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlin_coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$kotlin_coroutines_version")
    // AstraLibs
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    compileOnly("org.spigotmc:spigot-api:$spigot")
    compileOnly("org.spigotmc:spigot:$spigot")
    compileOnly("org.spigotmc:spigot-api:$spigot")
    compileOnly("com.github.MilkBowl:VaultAPI:$vault")
    testImplementation("junit:junit:4.13.1")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.18:1.24.1")
    testImplementation("io.kotest:kotest-runner-junit5:latest.release")
    testImplementation("io.kotest:kotest-assertions-core:latest.release")
    testImplementation(kotlin("test"))
}


tasks{
    withType<JavaCompile>() {
        options.encoding = "UTF-8"
    }
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    withType<Jar> {
        archiveClassifier.set("min")
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    test {
        useJUnit()
        testLogging {
            events("passed", "skipped", "failed")
            this.showStandardStreams = true
        }
    }
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand(
                    "name" to project.name,
                    "version" to project.version,
                    "description" to project.description
                )
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }
}
tasks.shadowJar {
    dependencies {
        include(dependency(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", ".aar")))))
        include(dependency("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"))
        include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlin_coroutines_version"))
        include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$kotlin_coroutines_version"))
    }
    isReproducibleFileOrder = true
    mergeServiceFiles()
    dependsOn(configurations)
    archiveClassifier.set(null as String?)
    from(sourceSets.main.get().output)
    from(project.configurations.runtimeClasspath)
    minimize()
    destinationDirectory.set(File("D:\\Minecraft Servers\\1_19\\paper\\plugins"))
}
