package dev.nikomaru.advancedshopfinder.utils.data

import dev.nikomaru.advancedshopfinder.AdvancedShopFinder
import dev.nikomaru.advancedshopfinder.data.FindOption
import dev.nikomaru.advancedshopfinder.files.Config.json
import dev.nikomaru.advancedshopfinder.files.PlayerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PlayerConfigUtils: KoinComponent {
    private val plugin: AdvancedShopFinder by inject()
    private suspend fun Player.getPlayerConfig(): PlayerConfig = withContext(Dispatchers.IO) {
        val player = this@getPlayerConfig
        val file = plugin.dataFolder.resolve("playerdata/${player.uniqueId}.json")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            file.writeText(json.encodeToString(PlayerConfig()))
        }
        PlayerConfig(player.toString(), hashMapOf())
    }

    suspend fun Player.getPlayerFindOption(): FindOption? {
        val config = this.getPlayerConfig()
        return config.findOptions[config.setting]
    }
}