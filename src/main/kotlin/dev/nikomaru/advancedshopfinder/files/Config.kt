package dev.nikomaru.advancedshopfinder.files

import dev.nikomaru.advancedshopfinder.AdvancedShopFinder.Companion.plugin
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Config {
    lateinit var config: ConfigData
    lateinit var json: Json
    fun loadConfig() {
        json = Json {
            prettyPrint = true
            isLenient = true
        }
        val configFile = plugin.dataFolder.resolve("config.json")

        if (!configFile.exists()) {
            val defaultConfigData = ConfigData(arrayListOf(
                PlaceData(-532, -85, "もりもと"),
            ))
            plugin.dataFolder.mkdir()
            configFile.createNewFile()
            configFile.writeText(json.encodeToString(defaultConfigData))
        }

        config = json.decodeFromString<ConfigData>(configFile.readText())
    }
}

@Serializable
data class ConfigData(
    val placeData: List<PlaceData>,
)

@Serializable
data class PlaceData(
    val x: Int,
    val z: Int,
    val placeName: String,
)