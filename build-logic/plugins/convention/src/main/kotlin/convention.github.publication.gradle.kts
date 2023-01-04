import gradle.kotlin.dsl.accessors._89ec0d158481bcae4dcd9c3c9b1a7e18.sourceSets
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.`maven-publish`
import org.gradle.kotlin.dsl.registering
import org.gradle.kotlin.dsl.signing
import java.util.*

plugins {
    `maven-publish`
    signing
}
// Stub secrets to let the project sync and build without the publication values set up
ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKeyRingFile"] = null
ext["ossrhUsername"] = null
ext["ossrhPassword"] = null

// Grabbing secrets from local.properties file or from environment variables, which could be used on CI
val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply {
            load(it)
        }
    }.onEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
}
fun getExtraString(name: String) = ext[name]?.toString()
println("signing.keyId: ${getExtraString("signing.keyId")}")
println("signing.password: ${getExtraString("signing.password")}")
println("signing.secretKeyRingFile: ${getExtraString("signing.secretKeyRingFile")}")
println("ossrhUsername: ${getExtraString("ossrhUsername")}")
println("ossrhPassword: ${getExtraString("ossrhPassword")}")

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}


artifacts {
//    archives(javadocJar)
    archives(tasks["sourcesJar"])
    archives(sourceSets.getByName("main"))
}

publishing {
    repositories {
        maven {
            name = "sonatype"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = getExtraString("ossrhUsername")
                password = getExtraString("ossrhPassword")
            }

        }

        maven {
            name = "sonatype"
            setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            credentials {
                username = getExtraString("ossrhUsername")
                password = getExtraString("ossrhPassword")
            }
        }
    }

    publications.create<MavenPublication>("default") {

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