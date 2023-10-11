package dev.nikomaru.advancedshopfinder.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import com.ghostchu.quickshop.api.shop.ShopType
import dev.nikomaru.advancedshopfinder.AdvancedShopFinder
import dev.nikomaru.advancedshopfinder.commands.ShopFindCommand.Companion.getBuyingShopCount
import dev.nikomaru.advancedshopfinder.commands.ShopFindCommand.Companion.getPlayerDistance
import dev.nikomaru.advancedshopfinder.commands.ShopFindCommand.Companion.getSellingShopCount
import dev.nikomaru.advancedshopfinder.files.Config
import dev.nikomaru.advancedshopfinder.utils.coroutines.minecraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import kotlin.math.hypot


@CommandMethod("advancedshopfinder|asf|shopfinder|sf")
class EnchantFindCommand {
    @CommandMethod("bookfind <enchantment>")
    suspend fun enchantFind(sender: CommandSender, @Argument("enchantment") enchantment: Enchantment) {
        val shop = AdvancedShopFinder.quickShop.shopManager.allShops.filter {
            it.item.type == Material.ENCHANTED_BOOK && (it.item.itemMeta as EnchantmentStorageMeta).hasStoredEnchant(
                enchantment
            )
        }

        if (shop.isEmpty()) {
            sender.sendRichMessage("検索結果: 0件")
            return
        }
        var message = Component.text("")
        var sum = 0

        val sell =
            shop.filter { it.shopType == ShopType.SELLING && (withContext(Dispatchers.minecraft) { it.remainingStock } > 0 || it.isUnlimited) }
                .sortedBy { it.price / it.shopStackingAmount }
        sell.forEach { shopChest ->
            val nearPlace = getNearPlace(shopChest)
            val nearTownDistance = hypot(
                nearPlace!!.x.toDouble() - shopChest.location.blockX, nearPlace.z.toDouble() - shopChest.location.blockZ
            )
            val playerLocation =
                if (sender is Player) sender.location else Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)
            val distance = getPlayerDistance(playerLocation, shopChest)
            val count = getSellingShopCount(shopChest)


            val mm = MiniMessage.miniMessage()
            val tags = arrayOf(
                Placeholder.component(
                    "player-name", Component.text(Bukkit.getOfflinePlayer(shopChest.owner).name.toString())
                ),
                Placeholder.component("price", Component.text(shopChest.price.toString())),
                Placeholder.component("shop-stacking-amount", Component.text(shopChest.shopStackingAmount.toString())),
                Placeholder.component("count", Component.text(count)),
                Placeholder.component("world", Component.text(shopChest.location.world.name)),
                Placeholder.component("x", Component.text(shopChest.location.blockX.toString())),
                Placeholder.component("y", Component.text(shopChest.location.blockY.toString())),
                Placeholder.component("z", Component.text(shopChest.location.blockZ.toString())),
                Placeholder.component("distance", Component.text(distance.toString())),
                Placeholder.component("near-town", mm.deserialize(nearPlace.placeName)),
                Placeholder.component("near-town-distance", Component.text(nearTownDistance.toInt().toString())),
                Placeholder.component("shop-type", mm.deserialize("<color:green>販売")),
                Placeholder.component("enchantment", mm.deserialize(""))
            )

            message = message.append(
                mm.deserialize(
                    Config.config.format, *tags
                )
            )
            sum++
        }
        val buy =
            shop.filter { it.shopType == ShopType.BUYING && (withContext(Dispatchers.minecraft) { it.remainingSpace } > 0 || it.isUnlimited) }
                .sortedBy { -it.price / it.shopStackingAmount }
        buy.forEach { shopChest ->

            val nearPlace = getNearPlace(shopChest)
            val nearTownDistance = hypot(
                nearPlace!!.x.toDouble() - shopChest.location.blockX, nearPlace.z.toDouble() - shopChest.location.blockZ
            )
            val playerLocation =
                if (sender is Player) sender.location else Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)
            val distance = getPlayerDistance(playerLocation, shopChest)
            val count = getBuyingShopCount(shopChest)
            val mm = MiniMessage.miniMessage()
            val tags = arrayOf(
                Placeholder.component(
                    "player-name", Component.text(Bukkit.getOfflinePlayer(shopChest.owner).name.toString())
                ),
                Placeholder.component("price", Component.text(shopChest.price.toString())),
                Placeholder.component("shop-stacking-amount", Component.text(shopChest.shopStackingAmount.toString())),
                Placeholder.component("count", Component.text(count)),
                Placeholder.component("world", Component.text(shopChest.location.world.name)),
                Placeholder.component("x", Component.text(shopChest.location.blockX.toString())),
                Placeholder.component("y", Component.text(shopChest.location.blockY.toString())),
                Placeholder.component("z", Component.text(shopChest.location.blockZ.toString())),
                Placeholder.component("distance", Component.text(distance.toString())),
                Placeholder.component("near-town", mm.deserialize(nearPlace.placeName)),
                Placeholder.component("near-town-distance", Component.text(nearTownDistance.toInt().toString())),
                Placeholder.component("shop-type", mm.deserialize("<color:red>買取")),
                Placeholder.component("enchantment", mm.deserialize(""))
            )

            message = message.append(
                mm.deserialize(
                    Config.config.format, *tags
                )
            )
            sum++
        }
        sender.sendRichMessage("<color:green>検索結果: ${sum}件")
        sender.sendMessage(message)

    }


}