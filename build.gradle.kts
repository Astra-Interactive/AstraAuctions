import java.util.Properties
import java.io.FileInputStream

plugins {
    java
    `maven-publish`
    `java-library`
    kotlin("jvm") version "1.5.21"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}
var gprUser: String? = null
var gprPassword: String? = null
task("Get GPR keys") {
    val astraPropsFile = file("astra.properties")
    if (!astraPropsFile.exists())
        astraPropsFile.createNewFile()
    val astraProps = Properties().apply { load(FileInputStream(astraPropsFile)) }
    gprUser = astraProps.getProperty("gpr.user")
    gprPassword = astraProps.getProperty("gpr.password")
    if (gprUser == null || gprPassword == null) {
        if (gprUser == null)
            astraProps.setProperty("gpr.user", "SET_GPR_USERNAME_HERE")
        if (gprPassword == null)
            astraProps.setProperty("gpr.password", "SET_GPR_KEY_HERE")
        astraProps.store(astraPropsFile.outputStream(), "")
        throw GradleException("You need to set your GPR keys")
    }
}
repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://nexus.scarsz.me/content/groups/public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.essentialsx.net/snapshots/")
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo1.maven.org/maven2/")
    maven("https://maven.playpro.com")
    maven("https://jitpack.io")
    flatDir {
        dirs("libs")
    }
}

dependencies {
    val spigot = "1.19-R0.1-SNAPSHOT"
    val vault = "1.7"
    val placeholderapi = "2.11.1"
    val protocolLib = "4.8.0"
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
//    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:$spigot")
//    compileOnly("com.comphenix.protocol:ProtocolLib:$protocolLib")
//    compileOnly("me.clip:placeholderapi:$placeholderapi")
    compileOnly("com.github.MilkBowl:VaultAPI:$vault")
    testImplementation("junit:junit:4.13.1")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.18:1.24.1")
    testImplementation("io.kotest:kotest-runner-junit5:latest.release")
    testImplementation("io.kotest:kotest-assertions-core:latest.release")
    testImplementation(kotlin("test"))
}

group = "com.astrainteractive"
version = "1.0.2"
val name = "AstraMarket"
description = "Global Market plugin from AstraInteractive for EmpireSMP"

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
tasks {
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

    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        dependencies {
//            include(dependency("com.astrainteractive:astralibs:1.1.9-8"))
            include(dependency(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar",".aar")))))
            include(dependency("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21"))
            include(dependency("org.jetbrains.kotlin:kotlin-runtime:1.5.21"))
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib:1.5.21"))
            include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1"))
            include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.1"))
        }
        isReproducibleFileOrder = true

        from(sourceSets.main.get().output)
        from(project.configurations.runtimeClasspath)
        manifest.attributes("Main-Class" to "com.astrainteractive.astratemplate.AstraTemplate")
        minimize()
    }

    test {
        useJUnit()
        testLogging {
            events("passed", "skipped", "failed")
            this.showStandardStreams = true
        }
    }

//    register<Copy>("copyToServer") {
//        val path = project.property("targetDir") ?: ""
//        if (path.toString().isEmpty()) {
//            println("targetDir is not set in gradle properties")
//            return@register
//        }
//        destinationDir = File(path.toString())
//    }
}