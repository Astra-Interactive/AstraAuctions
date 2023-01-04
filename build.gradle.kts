group = libs.versions.group.get()
version = libs.versions.plugin.get()
description = libs.versions.description.get()

plugins {
    java
    `java-library`
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.shadow) apply false
    id("convention.publication") apply false
    alias(libs.plugins.kotlin.dokka) apply false
}