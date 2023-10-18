package dev.nikomaru.advancedshopfinder

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.ghostchu.quickshop.api.QuickShopAPI
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import dev.nikomaru.advancedshopfinder.commands.EnchantFindCommand
import dev.nikomaru.advancedshopfinder.commands.HelpCommand
import dev.nikomaru.advancedshopfinder.commands.ReloadCommand
import dev.nikomaru.advancedshopfinder.commands.ShopFindCommand
import dev.nikomaru.advancedshopfinder.files.Config
import dev.nikomaru.advancedshopfinder.files.TranslateMap
import dev.nikomaru.advancedshopfinder.utils.command.EnchantmentParser.enchantmentSupport
import dev.nikomaru.advancedshopfinder.utils.command.ItemNameSuggestion
import dev.nikomaru.advancedshopfinder.utils.command.MaterialParser.materialSupport
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import org.bukkit.Material
import revxrsal.commands.autocomplete.SuggestionProvider
import revxrsal.commands.bukkit.BukkitCommandHandler
import revxrsal.commands.command.CommandActor
import revxrsal.commands.command.CommandParameter
import revxrsal.commands.command.ExecutableCommand
import revxrsal.commands.ktx.supportSuspendFunctions


class AdvancedShopFinder : SuspendingJavaPlugin() {

    companion object {
        lateinit var plugin: AdvancedShopFinder
            private set
        lateinit var quickShop: QuickShopAPI
            private set
        lateinit var translateData: Map<String, String>
            private set

        lateinit var protocolManager: ProtocolManager
            private set
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun onEnable() {
        // Plugin startup logic
        plugin = this
        quickShop = QuickShopAPI.getInstance()
        Config.loadConfig()
        setCommand()
        val br = this.javaClass.classLoader.getResourceAsStream("ja_JP.json")!!
        translateData = Config.json.decodeFromStream<TranslateMap>(br).map
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun setCommand() {

        val handler = BukkitCommandHandler.create(this)

        handler.setSwitchPrefix("--")
        handler.setFlagPrefix("--")
        handler.supportSuspendFunctions()

        //Enchantment
        handler.enchantmentSupport()
        //Material
        handler.materialSupport()

        handler.autoCompleter.registerSuggestionFactory { parameter: CommandParameter ->
            if (parameter.hasAnnotation(ItemNameSuggestion::class.java)) {
                return@registerSuggestionFactory SuggestionProvider { _: List<String>, _: CommandActor, _: ExecutableCommand ->
                    Material.values().map {
                        translateData[it.translationKey()] ?: it.translationKey()
                    } + Material.values().map { it.name.lowercase() }
                }
            }
            null
        }

        handler.setHelpWriter { command, actor ->
            java.lang.String.format(
                """
                <color:yellow>コマンド: <color:gray>%s
                <color:yellow>使用方法: <color:gray>%s
                <color:yellow>説明: <color:gray>%s
                
                """.trimIndent(),
                command.path.toList(),
                command.usage,
                command.description,
            )
        }


        with(handler) {
            register(ShopFindCommand())
            register(ReloadCommand())
            register(EnchantFindCommand())
            register(HelpCommand())
        }
        handler.registerBrigadier()
    }
}