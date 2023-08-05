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
    val format : String = "{11}: オーナー: <color:yellow>{0} <color:green>値段: <color:yellow>{1}/{2}個 <color:green>在庫: <color:yellow>{3}  <color:green>座標: <color:yellow>{4} x:{5} y:{6} z:{7} <color:green>距離: <color:yellow>{8}ブロック <color:green>最寄り: <color:yellow>{9}から{10}ブロック"

)

@Serializable
data class PlaceData(
    val x: Int,
    val z: Int,
    val placeName: String,
)