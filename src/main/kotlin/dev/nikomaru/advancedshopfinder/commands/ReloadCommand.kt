package dev.nikomaru.advancedshopfinder.commands

import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import dev.nikomaru.advancedshopfinder.files.Config
import org.bukkit.command.CommandSender

@CommandMethod("advancedshopfinder|asf|shopfinder|sf")
@CommandPermission("advancedshopfinder.admin")
class ReloadCommand {
    @CommandMethod("reload")
    fun reload(sender: CommandSender) {
        Config.loadConfig()
        sender.sendRichMessage("<color:green>コンフィグをリロードしました")
    }
}