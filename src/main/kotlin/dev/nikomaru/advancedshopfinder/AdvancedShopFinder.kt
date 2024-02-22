package dev.nikomaru.advancedshopfinder

import com.comphenix.protocol.ProtocolLibrary
import com.ghostchu.quickshop.api.QuickShopAPI
import dev.nikomaru.advancedshopfinder.commands.EnchantFindCommand
import dev.nikomaru.advancedshopfinder.commands.HelpCommand
import dev.nikomaru.advancedshopfinder.commands.ReloadCommand
import dev.nikomaru.advancedshopfinder.commands.ShopFindCommand
import dev.nikomaru.advancedshopfinder.files.server.Config
import dev.nikomaru.advancedshopfinder.files.server.TranslateMap
import dev.nikomaru.advancedshopfinder.utils.command.EnchantmentParser.enchantmentSupport
import dev.nikomaru.advancedshopfinder.utils.command.ItemNameSuggestion
import dev.nikomaru.advancedshopfinder.utils.command.MaterialParser.materialSupport
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import revxrsal.commands.autocomplete.SuggestionProvider
import revxrsal.commands.bukkit.BukkitCommandHandler
import revxrsal.commands.command.CommandActor
import revxrsal.commands.command.CommandParameter
import revxrsal.commands.command.ExecutableCommand
import revxrsal.commands.ktx.supportSuspendFunctions


open class AdvancedShopFinder: JavaPlugin() {
    private lateinit var translateData: TranslateMap

    @OptIn(ExperimentalSerializationApi::class)
    override fun onEnable() { // Plugin startup logic
        setupKoin()
        Config.loadConfig()
        setCommand()
        val br = this.javaClass.classLoader.getResourceAsStream("ja_JP.json")!!
        translateData = Config.json.decodeFromStream<TranslateMap>(br)
    }

    private fun setupKoin() {
        GlobalContext.getOrNull() ?: GlobalContext.startKoin {
            modules(module {
                single { this@AdvancedShopFinder }
                single { QuickShopAPI.getInstance() }
                single { ProtocolLibrary.getProtocolManager() }
                single { translateData }
            })
        }
    }

    override fun onDisable() { // Plugin shutdown logic
    }

    private fun setCommand() {
        val handler = BukkitCommandHandler.create(this)

        handler.setFlagPrefix("--")
        handler.setSwitchPrefix("--")

        handler.supportSuspendFunctions() //Enchantment
        handler.enchantmentSupport() //Material
        handler.materialSupport()

        handler.autoCompleter.registerSuggestionFactory { parameter: CommandParameter ->
            if (parameter.hasAnnotation(ItemNameSuggestion::class.java)) {
                return@registerSuggestionFactory SuggestionProvider { _: List<String>, _: CommandActor, _: ExecutableCommand ->
                    Material.entries.map {
                        translateData.map[it.translationKey()] ?: it.translationKey()
                    } + Material.entries.map { it.name.lowercase() }
                }
            }
            null
        }

        handler.setHelpWriter { command, _ ->
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
            register(ShopFindCommand)
            register(ReloadCommand())
            register(EnchantFindCommand())
            register(HelpCommand())
        }
    }
}