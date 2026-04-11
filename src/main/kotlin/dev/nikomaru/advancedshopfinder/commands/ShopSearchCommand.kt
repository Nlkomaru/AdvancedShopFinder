package dev.nikomaru.advancedshopfinder.commands

import dev.nikomaru.advancedshopfinder.search.ItemSearcher
import dev.nikomaru.advancedshopfinder.services.ShopListPresenter
import dev.nikomaru.advancedshopfinder.utils.data.FindOption
import dev.nikomaru.advancedshopfinder.utils.data.PlayerFindOptionUtils.getPlayerFindOption
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.koin.core.component.KoinComponent

@Command("advancedshopfinder|asf|shopfinder|sf")
object ShopSearchCommand : KoinComponent {
    private val presenter = ShopListPresenter()

    @Command("search <item>")
    @CommandDescription("アイテムを検索します")
    suspend fun searchItem(
        sender: CommandSender,
        @Argument("item") itemArray: Array<Material>,
    ) {
        val playerFindOption = (sender as? Player)?.getPlayerFindOption() ?: FindOption()
        val searcher = ItemSearcher()
        val header =
            MiniMessage.miniMessage().deserialize("<color:green><lang:${itemArray.first().translationKey()}>を検索中")
        searcher
            .search(itemArray)
            .mapLeft {
                sender.sendRichMessage(it.message)
            }.map { shops ->
                presenter.present(sender, shops, playerFindOption, header)
            }
    }
}
