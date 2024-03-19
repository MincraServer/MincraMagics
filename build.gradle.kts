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
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
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

    maven { url = uri("https://maven.enginehub.org/repo/") }
}

val pluginVersion = project.properties["pluginVersion"];
val paperVersion = project.properties["paperVersion"];
val oraxenVersion = project.properties["oraxenVersion"];

dependencies {
    // mincramagics
    compileOnly("io.papermc.paper:paper-api:${paperVersion}")
//    implementation("xyz.xenondevs:particle:1.8.4") deprecated!
    implementation("de.tr7zw:item-nbt-api-plugin:2.12.2")
    compileOnly("io.lumine:Mythic-Dist:5.3.5")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.3.0")
    compileOnly("me.clip:placeholderapi:2.11.4")
//    compileOnly("com.github.oraxen:oraxen:${oraxenVersion}")
    // ezsvg
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
    implementation("org.w3c:dom:2.3.0-jaxb-1.0.6")
    // Test
    testImplementation("io.papermc.paper:paper-api:${paperVersion}")
    implementation(files("libs/oraxen-${oraxenVersion}.jar"))
    // Spigot and NMS
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // WorldGuard
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
}

group = "jp.mincra"
version = pluginVersion as String
description = "MincraMagics"
java.sourceCompatibility = JavaVersion.VERSION_17

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
