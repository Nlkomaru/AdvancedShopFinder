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
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/" )
}
val paperVersion = "1.20.1-R0.1-SNAPSHOT"
val cloudVersion = "1.8.3"
val vaultVersion = "1.7"
val mccoroutineVersion = "2.13.0"
val quickShopVersion = "4.2.2.9"
val lampVersion = "3.1.7"

dependencies {
    compileOnly("io.papermc.paper", "paper-api", paperVersion)

    library(kotlin("stdlib"))

    compileOnly("com.github.MilkBowl", "VaultAPI", vaultVersion)

    compileOnly("com.comphenix.protocol", "ProtocolLib", "5.1.0")

    compileOnly("com.ghostchu", "quickshop-bukkit", quickShopVersion)
    compileOnly("com.ghostchu", "quickshop-api", quickShopVersion)

    implementation("com.github.Revxrsal.Lamp","common",lampVersion)
    implementation("com.github.Revxrsal.Lamp","bukkit",lampVersion)

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.7.3")
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.5.1")

    implementation("com.github.shynixn.mccoroutine", "mccoroutine-bukkit-api", mccoroutineVersion)
    implementation("com.github.shynixn.mccoroutine", "mccoroutine-bukkit-core", mccoroutineVersion)

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
    name = "AdvancedShopFinder" // need to change
    version = "miencraft_plugin_version"
    website = "https://github.com/Nlkomaru/AdvancedShopFinder"  // need to change
    description = "Quickshop finder"

    main = "$group.advancedshopfinder.AdvancedShopFinder"  // need to change

    depend = listOf("QuickShop-Hikari") // need to change

    apiVersion = "1.20"
    libraries = listOf(
        "com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.11.0",
        "com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.11.0"
    )
}

tasks.register("depsize") {
    description = "Prints dependencies for \"default\" configuration"
    doLast {
        listConfigurationDependencies(configurations["default"])
    }
}

tasks.register("depsize-all-configurations") {
    description = "Prints dependencies for all available configurations"
    doLast {
        configurations
            .filter { it.isCanBeResolved }
            .forEach { listConfigurationDependencies(it) }
    }
}


fun listConfigurationDependencies(configuration: Configuration) {
    val formatStr = "%,10.2f"

    val size = configuration.map { it.length() / (1024.0 * 1024.0) }.sum()

    val out = StringBuffer()
    out.append("\nConfiguration name: \"${configuration.name}\"\n")
    if (size > 0) {
        out.append("Total dependencies size:".padEnd(65))
        out.append("${String.format(formatStr, size)} Mb\n\n")

        configuration.sortedBy { -it.length() }
            .forEach {
                out.append(it.name.padEnd(65))
                out.append("${String.format(formatStr, (it.length() / 1024.0))} kb\n")
            }
    } else {
        out.append("No dependencies found")
    }
    println(out)
}
