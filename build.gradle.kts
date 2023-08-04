plugins {
    id("java")
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    kotlin("plugin.serialization") version "1.9.0"
}

group = "dev.nikomaru"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://plugins.gradle.org/m2/")
    maven("https://repo.incendo.org/content/repositories/snapshots")
}
val paperVersion = "1.20.1-R0.1-SNAPSHOT"
val cloudVersion = "1.7.1"
val vaultVersion = "1.7"
val mccoroutineVersion = "2.11.0"

dependencies {
    compileOnly("io.papermc.paper", "paper-api", paperVersion)

    library(kotlin("stdlib"))

    compileOnly("com.github.MilkBowl", "VaultAPI", vaultVersion)

    implementation("cloud.commandframework", "cloud-core", cloudVersion)
    implementation("cloud.commandframework", "cloud-kotlin-extensions", cloudVersion)
    implementation("cloud.commandframework", "cloud-paper", cloudVersion)
    implementation("cloud.commandframework", "cloud-annotations", cloudVersion)
    implementation("cloud.commandframework", "cloud-kotlin-coroutines-annotations", cloudVersion)
    implementation("cloud.commandframework", "cloud-kotlin-coroutines", cloudVersion)

    library("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.7.1")
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.5.1")

    library("com.github.shynixn.mccoroutine", "mccoroutine-bukkit-api", mccoroutineVersion)
    library("com.github.shynixn.mccoroutine", "mccoroutine-bukkit-core", mccoroutineVersion)
}

java {

toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
compileKotlin {
kotlinOptions.jvmTarget = "17"
kotlinOptions.javaParameters = true
}
compileTestKotlin {
kotlinOptions.jvmTarget = "17"
}
build {
dependsOn(shadowJar)
}
}
tasks.withType<JavaCompile>().configureEach {
options.encoding = "UTF-8"
}

tasks {
runServer {
minecraftVersion("1.20.1")
}
}


bukkit {
    name = "Template" // need to change
    version = "miencraft_plugin_version"
    website = "https://github.com/Nlkomaru/NoticeTemplate"  // need to change

    main = "$group.template.Template"  // need to change

    apiVersion = "1.20"
    libraries = listOf("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.11.0",
        "com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.11.0")
}