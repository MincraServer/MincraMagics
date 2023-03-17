/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        url = uri("https://mvn.lumine.io/repository/maven-public/")
    }

    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    maven {
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    // mincramagics
    implementation("xyz.xenondevs:particle:1.8.3")
    api("de.tr7zw:item-nbt-api-plugin:2.11.1")
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("io.lumine:Mythic-Dist:5.2.0")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")
    compileOnly("dev.jorel:commandapi-core:8.7.0")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("com.github.oraxen:oraxen:1.152.5")
    // ezsvg
    implementation("javax.xml.bind:jaxb-api:2.2.4")
    implementation("org.w3c:dom:2.3.0-jaxb-1.0.6")
}

group = "jp.mincra"
version = "0.1-SNAPSHOT"
description = "MincraMagics"
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
