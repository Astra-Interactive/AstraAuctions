import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import java.util.*

plugins {
    `maven-publish`
    signing
    java
    `java-library`
}

// Grabbing secrets from local.properties file or from environment variables, which could be used on CI
val secretPropsFile = project.rootProject.file(".gradle/gradle.properties")
val properties = Properties().apply { load(secretPropsFile.reader()) }
val signingKeyId = properties.getProperty("signing.keyId")
val signingPassword = properties.getProperty("signing.password")
val signingSecretKeyRingFile = properties.getProperty("signing.secretKeyRingFile")
val ossrhUsername = properties.getProperty("ossrhUsername")
val ossrhPassword = properties.getProperty("ossrhPassword")

ext["signing.keyId"] = signingKeyId
ext["signing.password"] = signingPassword
ext["signing.secretKeyRingFile"] = signingSecretKeyRingFile
ext["ossrhUsername"] = ossrhUsername
ext["ossrhPassword"] = ossrhPassword

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}
artifacts {
    archives(javadocJar)
}
java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    repositories {
        maven {
            name = "sonatype"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }

        }

        maven {
            name = "sonatype"
            setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }

    publications.create<MavenPublication>("default") {

        artifact(javadocJar.get())
        artifact(tasks["sourcesJar"])

        from(components["kotlin"])
        pom {
            artifactId = project.name
            groupId = libs.versions.group.get()
            version = libs.versions.plugin.get()
            name.set(project.name)
            description.set("Spigot core library written in kotlin")
            url.set("https://github.com/Astra-Interactive/AstraLibs")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://github.com/Astra-Interactive/AstraLibs/blob/main/LICENSE.md")
                }
            }
            developers {
                developer {
                    id.set("makeevrserg")
                    name.set("Roman Makeev")
                    email.set("makeevrserg@gmail.com")
                }
            }
            scm {
                connection.set("scm:git:git:github.com/Astra-Interactive/AstraLibs.git")
                developerConnection.set("scm:git:ssh://github.com/Astra-Interactive/AstraLibs.git")
                url.set("https://github.com/Astra-Interactive/AstraLibs")
            }
        }
    }
}

signing {
    sign(publishing.publications)
}