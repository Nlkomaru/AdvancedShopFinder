package dev.nikomaru.advancedshopfinder

import com.ghostchu.quickshop.api.QuickShopAPI
import dev.nikomaru.advancedshopfinder.commands.EnchantFindCommand
import dev.nikomaru.advancedshopfinder.commands.ReloadCommand
import dev.nikomaru.advancedshopfinder.commands.ShopFindCommand
import dev.nikomaru.advancedshopfinder.files.Config
import dev.nikomaru.advancedshopfinder.files.TranslateMap
import dev.nikomaru.advancedshopfinder.utils.command.EnchantmentParser.enchantmentSupport
import dev.nikomaru.advancedshopfinder.utils.command.ItemNameSuggestion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import revxrsal.commands.autocomplete.SuggestionProvider
import revxrsal.commands.bukkit.BukkitCommandHandler
import revxrsal.commands.command.CommandActor
import revxrsal.commands.command.CommandParameter
import revxrsal.commands.command.ExecutableCommand
import revxrsal.commands.ktx.supportSuspendFunctions


class AdvancedShopFinder : JavaPlugin() {

    companion object {
        lateinit var plugin: AdvancedShopFinder
            private set
        lateinit var quickShop: QuickShopAPI
            private set
        lateinit var translateData: Map<String, String>
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
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun setCommand() {

        val handler = BukkitCommandHandler.create(this)

        handler.setSwitchPrefix("--")
        handler.supportSuspendFunctions()
        //Enchantment
        handler.enchantmentSupport()

        handler.autoCompleter.registerSuggestionFactory { parameter: CommandParameter ->
            if (parameter.hasAnnotation(ItemNameSuggestion::class.java)) {
                return@registerSuggestionFactory SuggestionProvider { _: List<String>, _: CommandActor, _: ExecutableCommand ->
                    Material.values().map {
                        translateData[it.translationKey()] ?: it.translationKey()
                    } + Material.values().map { it.name.lowercase() }
                }
            }
            null // Parameter does not have @WithPermission, ignore it.
        }



        with(handler) {
            // write your command here
            register(ShopFindCommand())
            register(ReloadCommand())
            register(EnchantFindCommand())
        }
    }
}