package dev.nikomaru.advancedshopfinder.commands

import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Command

@Command("advancedshopfinder|asf|shopfinder|sf")
object HelpCommand {

    @Command("help")
    fun help(sender: CommandSender) {
        sender.sendMessage("§6§lAdvancedShopFinder §7- §fヘルプ")
        sender.sendRichMessage("<>[link]")
    }


}