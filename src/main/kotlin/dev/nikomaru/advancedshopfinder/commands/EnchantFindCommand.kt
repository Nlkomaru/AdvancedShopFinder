package dev.nikomaru.advancedshopfinder.commands

import com.ghostchu.quickshop.api.QuickShopAPI
import com.ghostchu.quickshop.api.shop.ShopType
import dev.nikomaru.advancedshopfinder.utils.ComponentUtils.toGsonText
import dev.nikomaru.advancedshopfinder.utils.ComponentUtils.toLegacyText
import dev.nikomaru.advancedshopfinder.utils.ComponentUtils.toMiniMessage
import dev.nikomaru.advancedshopfinder.utils.ComponentUtils.toPlainText
import dev.nikomaru.advancedshopfinder.utils.data.FindOption
import dev.nikomaru.advancedshopfinder.utils.data.PlayerFindOptionUtils.getPlayerFindOption
import dev.nikomaru.advancedshopfinder.utils.data.SortType
import dev.nikomaru.advancedshopfinder.utils.data.TextType
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import revxrsal.commands.annotation.*


@Command("advancedshopfinder", "asf", "shopfinder", "sf")
class EnchantFindCommand: KoinComponent {
    private val quickShop: QuickShopAPI by inject()

    @Subcommand("bookfind")
    suspend fun enchantFind(
        sender: CommandSender,
        enchantment: Enchantment,
        @Flag("sort-type") @Default("ASC_PRICE_PER_STACK") sortType: SortType,
        @Flag("lightning-time") @Default("1000") @Range(min = 1.0, max = 60.0) lightningTime: Long,
        @Flag("lightning-interval") @Default("500") @Range(min = 1.0, max = 10.0) lightningInterval: Long,
        @Flag("lightning-count") @Default("5") @Range(min = 1.0, max = 20.0) lightningCount: Int,
        @Flag("lightning-distance") @Default("200") @Range(min = 1.0, max = 500.0) lightningDistance: Int,
        @Switch("disable-buying-chest") disableBuy: Boolean,
        @Switch("disable-selling-chest") disableSell: Boolean,
        @Switch("disable-lighting") disableLightning: Boolean,
    ) {
        val shop = quickShop.shopManager.allShops.filter {
            it.item.type == Material.ENCHANTED_BOOK && (it.item.itemMeta as EnchantmentStorageMeta).hasStoredEnchant(
                enchantment
            )
        }
        val options = (sender as? Player)?.getPlayerFindOption() ?: FindOption()

        if (shop.isEmpty()) {
            sender.sendRichMessage("検索結果: 0件")
            return
        }
        var message = Component.text("")
        var sum = 0

        if (!disableSell) {
            val (newMessage, newSum) = ShopFindCommand.processShops(
                shop, sender, message, sum, options, ShopType.SELLING
            )
            message = message.append(newMessage)
            sum = newSum
        }

        if (!disableBuy) {
            val (newMessage, newSum) = ShopFindCommand.processShops(
                shop, sender, message, sum, options, ShopType.BUYING
            )
            message = message.append(newMessage)
            sum = newSum
        }

        sender.sendRichMessage("<color:green><lang:${enchantment.translationKey()}> の検索結果: ${sum}件")
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