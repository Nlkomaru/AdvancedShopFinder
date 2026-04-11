package dev.nikomaru.advancedshopfinder.commands

import dev.nikomaru.advancedshopfinder.search.FuzzySearcher
import dev.nikomaru.advancedshopfinder.search.Searcher
import dev.nikomaru.advancedshopfinder.services.ShopListPresenter
import dev.nikomaru.advancedshopfinder.utils.data.FindOption
import dev.nikomaru.advancedshopfinder.utils.data.PlayerFindOptionUtils.getPlayerFindOption
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.koin.core.component.KoinComponent
import java.util.Locale

@Command("advancedshopfinder|asf|shopfinder|sf")
object FuzzySearchCommand : KoinComponent {
    private val presenter = ShopListPresenter()

    @Command("fuzzy-search <name>")
    suspend fun fuzzySearch(
        sender: CommandSender,
        @Argument("name") name: String,
    ) {
        val locale = if (sender is Player) sender.locale() else Locale.getDefault()
        val playerFindOption = (sender as? Player)?.getPlayerFindOption() ?: FindOption()
        val searcher: Searcher<Pair<String, Locale>> = FuzzySearcher()
        val header = MiniMessage.miniMessage().deserialize("<color:green>${name}をあいまい検索中")
        searcher
            .search(Pair(name, locale))
            .mapLeft {
                sender.sendRichMessage(it.message)
            }.map { shops ->
                presenter.present(sender, shops, playerFindOption, header)
            }
    }
}
