import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    java
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.resource.factory)
}

group = "dev.nikomaru"
version = "1.0-SNAPSHOT"
buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://plugins.gradle.org/m2/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
}


dependencies {
    implementation(gradleApi())
    implementation(localGroovy())

    compileOnly(libs.paper.api)

    implementation(libs.bundles.commands)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.bundles.coroutines)

    compileOnly(libs.vault.api)

    compileOnly(libs.protocol.lib)

    compileOnly(libs.quickshop.bukkit)
    compileOnly(libs.quickshop.api)

    implementation(libs.bundles.arrow)

    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)

    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mock.bukkit)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.bundles.koin.test)
}

kotlin {
    jvmToolchain {
        (this).languageVersion.set(JavaLanguageVersion.of(21))
    }
    jvmToolchain(21)
}


tasks {
    compileKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
        compilerOptions.javaParameters = true
        compilerOptions.languageVersion.set(KotlinVersion.KOTLIN_2_0)
    }
    compileTestKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }
    build {
        dependsOn(shadowJar)
    }
    runServer {
        minecraftVersion("1.21.4")
        downloadPlugins {
            modrinth("quickshop-hikari", "6.2.0.7")
            github("dmulloy2", "ProtocolLib", "5.3.0", "ProtocolLib.jar")
            url("https://ci.ender.zone/job/EssentialsX/lastSuccessfulBuild/artifact/jars/EssentialsX-2.21.0-dev+162-ea3ea20.jar")
            github("Milkbowl", "Vault", "1.7.3", "Vault.jar")
        }
    }
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
    test {
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
            events("passed", "skipped", "failed")
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

}

sourceSets.main {
    resourceFactory {
        bukkitPluginYaml {
            name = rootProject.name
            version = project.version.toString()
            website = "https://github.com/Nlkomaru/AdvancedShopFinder"
            main = "$group.advancedshopfinder.AdvancedShopFinder"
            apiVersion = "1.20"
            libraries = libs.bundles.coroutines.asString()
            depend = listOf("QuickShop-Hikari", "ProtocolLib")
        }
    }
}
//TODO add translations

tasks.register("generateTranslate", dev.nikomaru.tasks.GenerateTranslateTask::class)

fun Provider<ExternalModuleDependencyBundle>.asString(): List<String> {
    return this.get().map { dependency ->
        "${dependency.group}:${dependency.name}:${dependency.version}"
    }
}