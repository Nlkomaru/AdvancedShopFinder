package dev.nikomaru.tasks

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.net.URI

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

open class GenerateTranslateTask : DefaultTask() {

    @TaskAction
    fun runTask() {
        val resourceDir: Path =
            Paths.get(project.projectDir.absolutePath, "src", "main", "resources", "minecraft")
        if (!Files.exists(resourceDir)) {
            Files.createDirectories(resourceDir)
        }

        val url = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"

        val data = URI(url).toURL().readText()

        val gson = Gson()

        val version = gson.fromJson(data, JsonObject::class.java).get("latest").asJsonObject.get("release").asString

        println("latestVersion: $version")


        val langListUrl =
            "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/$version/assets/minecraft/lang/_list.json"


        val lang = URI(langListUrl).toURL().readText()

        val langJson = gson.fromJson(lang, JsonObject::class.java)

        val langList = langJson.get("files").asJsonArray.asList().map { it.asString.split(".")[0] }

        println("detectedLang: $langList")

        langList.parallelStream().forEach { lang ->
            val individualLangUri =
                "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/$version/assets/minecraft/lang/$lang.json"
            val text = URI(individualLangUri).toURL().readText()
            val map = gson.fromJson(text, Map::class.java)

            val translateMap = mutableMapOf<String, String>()
            map.filter { (t, _) ->
                t.toString().startsWith("item.") || t.toString().startsWith("block.") || t.toString()
                    .startsWith("enchantment.")
            }.forEach { (t, u) ->
                    translateMap += t.toString() to u.toString()
                }
            val translateMap2 = TranslateMap(translateMap)
            val json = Json {
                prettyPrint = true
                isLenient = true
                encodeDefaults = true
                ignoreUnknownKeys = true

            }.encodeToString(translateMap2)

            val output = resourceDir.resolve("$lang.json").toFile()
            output.parentFile.mkdirs()
            output.createNewFile()
            output.writeText(json.replace("\n","\r\n"))
        }
    }


}

@kotlinx.serialization.Serializable
data class TranslateMap(
    var map: Map<String, String>
)