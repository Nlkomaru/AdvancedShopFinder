package dev.nikomaru.advancedshopfinder.commands

import org.bukkit.command.CommandSender
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Default
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.help.CommandHelp


@Command("advancedshopfinder", "asf", "shopfinder", "sf")
object HelpCommand {
    @Subcommand("help")
    fun help(sender: CommandSender, helpEntries: CommandHelp<String>, @Default("1") page: Int) {
        for (entry in helpEntries.paginate(page, 5))  // 5 entries per page
            sender.sendRichMessage(entry)
    }
}