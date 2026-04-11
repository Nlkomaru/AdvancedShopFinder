package dev.nikomaru.advancedshopfinder.commands

import dev.nikomaru.advancedshopfinder.search.EnchantBookSearcher
import dev.nikomaru.advancedshopfinder.search.Searcher
import dev.nikomaru.advancedshopfinder.services.ShopListPresenter
import dev.nikomaru.advancedshopfinder.utils.data.FindOption
import dev.nikomaru.advancedshopfinder.utils.data.PlayerFindOptionUtils.getPlayerFindOption
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.koin.core.component.KoinComponent

@Command("advancedshopfinder|asf|shopfinder|sf")
object EnchantFindCommand : KoinComponent {
    private val presenter = ShopListPresenter()

    @Command("search-book <enchantment>")
    suspend fun searchEnchantmentBook(
        sender: CommandSender,
        @Argument("enchantment") enchantment: Enchantment,
    ) {
        val options = (sender as? org.bukkit.entity.Player)?.getPlayerFindOption() ?: FindOption()
        val searcher: Searcher<Enchantment> = EnchantBookSearcher()
        val header = MiniMessage.miniMessage().deserialize("<color:green>${enchantment.key.key}のエンチャント本を検索中")
        searcher
            .search(enchantment)
            .mapLeft {
                sender.sendRichMessage(it.message)
            }.map { shops ->
                presenter.present(sender, shops, options, header)
            }
    }
}
