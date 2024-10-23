package dev.nikomaru.advancedshopfinder.commands

import com.ghostchu.quickshop.api.QuickShopAPI
import com.ghostchu.quickshop.api.shop.ShopType
import dev.nikomaru.advancedshopfinder.files.server.ConfigData
import dev.nikomaru.advancedshopfinder.files.server.TranslateMap
import dev.nikomaru.advancedshopfinder.utils.ComponentUtils.toGsonText
import dev.nikomaru.advancedshopfinder.utils.ComponentUtils.toLegacyText
import dev.nikomaru.advancedshopfinder.utils.ComponentUtils.toMiniMessage
import dev.nikomaru.advancedshopfinder.utils.ComponentUtils.toPlainText
import dev.nikomaru.advancedshopfinder.utils.data.FindOption
import dev.nikomaru.advancedshopfinder.utils.data.PlayerFindOptionUtils.getPlayerFindOption
import dev.nikomaru.advancedshopfinder.utils.data.TextType
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

@Command("advancedshopfinder|asf|shopfinder|sf")
object FuzzySearchCommand: KoinComponent {
    private val translateData: TranslateMap by inject()
    private val quickShop: QuickShopAPI by inject()

    @Command("fuzzy-search <name>")
    suspend fun fuzzySearch(sender: CommandSender, @Argument("name") name: String) {
        val keyList = translateData.map.filter { (k, v) -> v.contains(name) || k.contains(name) }.map { (k, _) ->
            k.lowercase()
        }
        val options = (sender as? Player)?.getPlayerFindOption() ?: FindOption()
        val shop = quickShop.shopManager.allShops.filter {
            keyList.contains(it.item.type.translationKey().lowercase()) || it.item.displayName().toPlainText()
                .contains(name)
        }
        if (shop.isEmpty()) {
            sender.sendRichMessage("検索結果: 0件")
            return
        }
        if (shop.size > get<ConfigData>().fuzzySearchLimit) {
            sender.sendRichMessage("検索結果が多すぎます。絞り込んでください。")
            return
        }
        var message: Component = Component.text("")
        var sum = 0

        val (newSellMessage, newSellSum) = ShopSearchCommand.processShops(
            shop, sender, message, sum, options, ShopType.SELLING
        )
        message = newSellMessage
        sum = newSellSum


        val (newBuyMessage, newBuySum) = ShopSearchCommand.processShops(
            shop, sender, message, sum, options, ShopType.BUYING
        )
        message = newBuyMessage
        sum = newBuySum

        sender.sendRichMessage("<color:green>${name} の検索結果: ${sum}件")
        val textType = (sender as? Player)?.getPlayerFindOption()?.textType ?: TextType.COMPONENT
        when (textType) {
            TextType.COMPONENT -> sender.sendMessage(message)
            TextType.LEGACY -> sender.sendMessage(message.toLegacyText())
            TextType.GSON -> sender.sendMessage(message.toGsonText())
            TextType.PLAIN -> sender.sendMessage(message.toPlainText())
            TextType.MINI_MESSAGE -> sender.sendRichMessage(message.toMiniMessage())
            TextType.MINI_MESSAGE_RAW -> sender.sendMessage(message.toMiniMessage())
        }
    }
}