package dev.nikomaru.advancedshopfinder

import com.comphenix.protocol.ProtocolLibrary
import com.ghostchu.quickshop.api.QuickShopAPI
import dev.nikomaru.advancedshopfinder.commands.*
import dev.nikomaru.advancedshopfinder.commands.utils.parser.EnchantmentParser
import dev.nikomaru.advancedshopfinder.commands.utils.parser.MaterialArrayParser
import dev.nikomaru.advancedshopfinder.files.server.Config
import dev.nikomaru.advancedshopfinder.files.server.TranslateMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.kotlin.coroutines.annotations.installCoroutineSupport
import org.incendo.cloud.paper.LegacyPaperCommandManager
import org.incendo.cloud.setting.ManagerSetting
import org.koin.core.context.GlobalContext
import org.koin.dsl.module


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
        val commandManager = LegacyPaperCommandManager.createNative(
            this,
            ExecutionCoordinator.simpleCoordinator()
        )

        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions()
        }

        commandManager.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true)

        commandManager.parserRegistry().registerParser(MaterialArrayParser.materialArrayParser())
        commandManager.parserRegistry().registerParser(EnchantmentParser.enchantmentParser())


        val annotationParser = AnnotationParser(commandManager, CommandSender::class.java)
        annotationParser.installCoroutineSupport()

        with(annotationParser) {
            parse(
                EnchantFindCommand,
                FuzzySearchCommand,
                ReloadCommand,
                ShopSearchCommand
            )
        }
    }
}