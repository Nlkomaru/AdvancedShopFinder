package dev.nikomaru.advancedshopfinder.commands


import dev.nikomaru.advancedshopfinder.files.server.Config
import org.bukkit.command.CommandSender
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.bukkit.annotation.CommandPermission

@Command("advancedshopfinder", "asf", "shopfinder", "sf")
@CommandPermission("advancedshopfinder.admin")
object ReloadCommand {
    @Subcommand("reload")
    fun reload(sender: CommandSender) {
        Config.loadConfig()
        sender.sendRichMessage("<color:green>コンフィグをリロードしました")
    }
}