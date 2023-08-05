package dev.nikomaru.advancedshopfinder

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.kotlin.coroutines.annotations.installCoroutineSupport
import cloud.commandframework.meta.SimpleCommandMeta
import cloud.commandframework.paper.PaperCommandManager
import com.ghostchu.quickshop.api.QuickShopAPI
import dev.nikomaru.advancedshopfinder.commands.ReloadCommand
import dev.nikomaru.advancedshopfinder.commands.ShopFindCommand
import dev.nikomaru.advancedshopfinder.files.Config
import dev.nikomaru.advancedshopfinder.files.TranslateMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin


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
        val commandManager: PaperCommandManager<CommandSender> = PaperCommandManager(
            plugin,
            AsynchronousCommandExecutionCoordinator.builder<CommandSender>().build(),
            java.util.function.Function.identity(),
            java.util.function.Function.identity()
        )

        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions()
        }

        val annotationParser = AnnotationParser(commandManager, CommandSender::class.java) {
            SimpleCommandMeta.empty()
        }.installCoroutineSupport()

        with(annotationParser) {
            // write your command here
            parse(ShopFindCommand())
            parse(ReloadCommand())
        }
    }
}