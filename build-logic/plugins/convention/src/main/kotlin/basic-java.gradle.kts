plugins {
    java
    `java-library`
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = libs.versions.project.group.get()
version = libs.versions.project.version.get()
description = libs.versions.project.description.get()

java {
    withSourcesJar()
    withJavadocJar()
    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_17
}
// TODO
// kotlin {
//    jvmToolchain(11)
// }
tasks {
    withType<JavaCompile>() {
        options.encoding = "UTF-8"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.majorVersion
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
}
