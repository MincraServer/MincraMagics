plugins {
    `java-library`
    `maven-publish`

    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
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

    maven {
        url = uri("https://repo.oraxen.com/releases")
    }
}

val pluginVersion = project.properties["pluginVersion"]
val paperVersion = project.properties["paperVersion"]
val oraxenVersion = project.properties["oraxenVersion"]

dependencies {
    // =========================================================
    // Download dependent plugins and place them in libs folder.
    // Required plugins:
    // - GSit
    // - Jobs Reborn
    // - Oraxen
    // =========================================================

    compileOnly("io.papermc.paper:paper-api:${paperVersion}")
    // Dependent plugins
    implementation("de.tr7zw:item-nbt-api-plugin:2.12.2")
    compileOnly("io.lumine:Mythic-Dist:5.3.5")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.3.0")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.13")
    implementation(files("libs/Jobs5.2.6.3.jar")) // Jobs Reborn
    implementation(files("libs/oraxen-1.190.0-modified.jar")) // // 変更を加えた Oraxen を libs フォルダに配置
    compileOnly(files("libs/GSit-2.4.2.jar"))
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))) // NMS
    // Libraries
    implementation("org.hibernate.orm:hibernate-core:7.1.0.Final")
    implementation("org.xerial:sqlite-jdbc:3.50.3.0")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    // For jp.mincra.ezsvg
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
    implementation("org.w3c:dom:2.3.0-jaxb-1.0.6")

    // Test
    testImplementation("io.papermc.paper:paper-api:${paperVersion}")
}

group = "jp.mincra"
version = pluginVersion as String
description = "MincraMagics"
java.sourceCompatibility = JavaVersion.VERSION_21

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Javadoc> {
        options.encoding = "UTF-8"
    }

//    // パッケージの競合を回避
//    shadowJar {
//        relocate("org.hibernate", "jp.mincra.mincramagics.libs.hibernate")
//        relocate("org.sqlite", "jp.mincra.mincramagics.libs.sqlite")
//        relocate("jakarta.persistence", "jp.mincra.mincramagics.libs.jakarta.persistence")
//        // Add other relocations for transitive dependencies as needed
//
//        archiveBaseName.set(project.name)
//        archiveClassifier.set("")
//        archiveVersion.set(project.version.toString())
//    }

//    build {
//        dependsOn(shadowJar)
//    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
