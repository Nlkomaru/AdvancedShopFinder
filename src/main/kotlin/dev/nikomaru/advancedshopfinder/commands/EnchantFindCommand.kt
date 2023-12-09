package dev.nikomaru.advancedshopfinder.commands

import com.ghostchu.quickshop.api.shop.ShopType
import dev.nikomaru.advancedshopfinder.AdvancedShopFinder
import dev.nikomaru.advancedshopfinder.utils.data.FindOptions
import dev.nikomaru.advancedshopfinder.utils.data.SortType
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import revxrsal.commands.annotation.*


@Command("advancedshopfinder", "asf", "shopfinder", "sf")
class EnchantFindCommand {
    @Subcommand("bookfind")
    suspend fun enchantFind(
        sender: CommandSender,
        enchantment: Enchantment,
        @Flag("sort-type") @Default("ASC_PRICE_PER_STACK") sortType: SortType,
        @Flag("lightning-time") @Default("1000") @Range(min = 1.0 , max= 60.0) lightningTime: Long,
        @Flag("lightning-interval") @Default("500") @Range(min = 1.0 , max= 10.0) lightningInterval: Long,
        @Flag("lightning-count") @Default("5") @Range(min = 1.0 , max= 20.0) lightningCount: Int,
        @Flag("lightning-distance") @Default("200") @Range(min = 1.0 , max= 500.0) lightningDistance: Int,
        @Switch("disable-buying-chest") disableBuy: Boolean,
        @Switch("disable-selling-chest") disableSell: Boolean,
        @Switch("disable-lighting") disableLightning: Boolean,
    ) {
        val shop = AdvancedShopFinder.quickShop.shopManager.allShops.filter {
            it.item.type == Material.ENCHANTED_BOOK && (it.item.itemMeta as EnchantmentStorageMeta).hasStoredEnchant(
                enchantment
            )
        }

        val options = FindOptions(
            sortType,
            disableBuy,
            disableSell,
            disableLightning,
            lightningTime,
            lightningInterval,
            lightningCount,
            lightningDistance
        )

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
        sender.sendMessage(message)

    }


}