package dev.nikomaru.advancedshopfinder.utils.data

import dev.nikomaru.advancedshopfinder.AdvancedShopFinder
import dev.nikomaru.advancedshopfinder.files.player.PlayerFindOption
import dev.nikomaru.advancedshopfinder.files.server.Config.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PlayerFindOptionUtils: KoinComponent {
    private val plugin: AdvancedShopFinder by inject()

    suspend fun Player.getPlayerFindOption() = withContext(Dispatchers.IO) {
        val player = this@getPlayerFindOption
        val file = plugin.dataFolder.resolve("playerdata").resolve("${player.uniqueId}").resolve("config.json")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            file.writeText(json.encodeToString(PlayerFindOption()))
        }
        val config = json.decodeFromString<PlayerFindOption>(file.readText())
        config.findOptions[config.setting]
    }
}