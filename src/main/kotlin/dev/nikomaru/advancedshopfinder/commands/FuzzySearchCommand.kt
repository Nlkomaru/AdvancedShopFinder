package dev.nikomaru.advancedshopfinder.commands

import dev.nikomaru.advancedshopfinder.search.FuzzySearcher
import dev.nikomaru.advancedshopfinder.search.Searcher
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.koin.core.component.KoinComponent
import java.util.Locale

@Command("advancedshopfinder|asf|shopfinder|sf")
object FuzzySearchCommand: KoinComponent {
    @Command("fuzzy-search <name>")
    fun fuzzySearch(sender: CommandSender, @Argument("name") name: String) {
        val locale = if (sender is Player) sender.locale() else Locale.getDefault()
        val searcher: Searcher<Pair<String, Locale>> = FuzzySearcher()
        searcher
            .search(Pair(name, locale))
            .mapLeft {
                sender.sendMessage(it.message)
            }.map {
                //it.sortWith(playerFindOption.sortComparator).display(sender)

            }
    }
}