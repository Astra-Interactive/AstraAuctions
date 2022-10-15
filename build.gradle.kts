
group = "com.astrainteractive"
version = "1.1.2"
val name = "AstraMarket"
description = "Global Market plugin from AstraInteractive for EmpireSMP"

plugins {
    java
    `maven-publish`
    `java-library`
    kotlin("jvm") version "1.7.0"
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
    maven("https://m2.dv8tion.net/releases")
    maven("https://maven.playpro.com")
    maven("https://jitpack.io")
    maven {
        url = uri("https://maven.pkg.github.com/Astra-Interactive/AstraLibs")
        val config = project.getConfig()
        credentials {
            username = config.username
            password = config.token
        }
    }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${Dependencies.Kotlin.version}")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Kotlin.coroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${Dependencies.Kotlin.coroutines}")
    // Serialization
    implementation("org.jetbrains.kotlin:kotlin-serialization:${Dependencies.Kotlin.version}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Dependencies.Kotlin.json}")
    implementation("com.charleskorn.kaml:kaml:${Dependencies.Kotlin.kaml}")
    // AstraLibs
    implementation("ru.astrainteractive.astralibs:ktx-core:${Dependencies.Kotlin.astraLibs}")
    implementation("ru.astrainteractive.astralibs:spigot-core:${Dependencies.Kotlin.astraLibs}")
    implementation("org.bstats:bstats-bukkit:${Dependencies.Spigot.bstats}")
    compileOnly("io.papermc.paper:paper-api:${Dependencies.Spigot.version}")
    compileOnly("org.spigotmc:spigot-api:${Dependencies.Spigot.version}")
    compileOnly("org.spigotmc:spigot:${Dependencies.Spigot.version}")
    compileOnly("com.github.MilkBowl:VaultAPI:${Dependencies.Spigot.vault}")
    // Test
    testImplementation(kotlin("test"))
    testImplementation("org.testng:testng:7.1.0")
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
        include(dependency("org.jetbrains.kotlin:kotlin-gradle-plugin:${Dependencies.Kotlin.version}"))
        include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Kotlin.coroutines}"))
        include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${Dependencies.Kotlin.coroutines}"))
        include(dependency("org.bstats:bstats-bukkit:${Dependencies.Spigot.bstats}"))
    }
    relocate("org.bstats", "com.astrainteractive.astratemplate")
    isReproducibleFileOrder = true
    mergeServiceFiles()
    dependsOn(configurations)
    archiveClassifier.set(null as String?)
    from(sourceSets.main.get().output)
    from(project.configurations.runtimeClasspath)
    minimize()
    destinationDirectory.set(File("D:\\Minecraft Servers\\TEST_SERVER\\plugins"))
}
