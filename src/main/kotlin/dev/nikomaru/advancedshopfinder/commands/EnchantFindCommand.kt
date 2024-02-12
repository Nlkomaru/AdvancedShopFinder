package dev.nikomaru.advancedshopfinder.commands

import com.ghostchu.quickshop.api.QuickShopAPI
import com.ghostchu.quickshop.api.shop.ShopType
import dev.nikomaru.advancedshopfinder.data.FindOption
import dev.nikomaru.advancedshopfinder.utils.data.PlayerConfigUtils.getPlayerFindOption
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand


@Command("advancedshopfinder", "asf", "shopfinder", "sf")
class EnchantFindCommand: KoinComponent {
    private val quickShop: QuickShopAPI by inject()

    @Subcommand("bookfind")
    suspend fun enchantFind(
        sender: CommandSender, enchantment: Enchantment
    ) {
        val shop = quickShop.shopManager.allShops.filter {
            it.item.type == Material.ENCHANTED_BOOK && (it.item.itemMeta as EnchantmentStorageMeta).hasStoredEnchant(
                enchantment
            )
        }
        val options = if (sender is Player) {
            sender.getPlayerFindOption()
        } else {
            FindOption()
        } ?: FindOption()

        if (shop.isEmpty()) {
            sender.sendRichMessage("検索結果: 0件")
            return
        }
        var message: Component = Component.text("")
        var sum = 0

        val (newSellMessage, newSellSum) = ShopFindCommand.processShops(
            shop, sender, message, sum, options, ShopType.SELLING
        )
        message = newSellMessage
        sum = newSellSum


        val (newBuyMessage, newBuySum) = ShopFindCommand.processShops(
            shop, sender, message, sum, options, ShopType.BUYING
        )
        message = newBuyMessage
        sum = newBuySum

        sender.sendRichMessage("<color:green><lang:${enchantment.translationKey()}> の検索結果: ${sum}件")
        sender.sendMessage(message)
    }
}