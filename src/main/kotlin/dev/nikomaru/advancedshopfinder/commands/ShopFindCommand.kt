package dev.nikomaru.advancedshopfinder.commands

import com.ghostchu.quickshop.api.shop.Shop
import com.ghostchu.quickshop.api.shop.ShopType
import dev.nikomaru.advancedshopfinder.AdvancedShopFinder
import dev.nikomaru.advancedshopfinder.files.Config
import dev.nikomaru.advancedshopfinder.utils.command.ItemNameSuggestion
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
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.annotation.Switch
import kotlin.math.hypot


@Command("advancedshopfinder", "asf", "shopfinder", "sf")
class ShopFindCommand {
    @Subcommand("search")
    suspend fun searchItem(
        sender: CommandSender,
        @ItemNameSuggestion itemName: String,
        @Switch("disable-buying-chest") disableBuy: Boolean,
        @Switch("disable-selling-chest") disableSell: Boolean
    ) {
        val item =
            getKey(AdvancedShopFinder.translateData, itemName) ?: Material.matchMaterial(itemName)?.translationKey()


        val shop = AdvancedShopFinder.quickShop.shopManager.allShops.filter {
            it.item.type.translationKey().equals(item, true)
        }

        if (shop.isEmpty()) {
            sender.sendRichMessage("検索結果: 0件")
            return
        }

        var message = Component.text("")
        var sum = 0

        if (!disableSell) {
            val sell =
                shop.filter { it.shopType == ShopType.SELLING && (withContext(Dispatchers.minecraft) { it.remainingStock } > 0 || it.isUnlimited) }
                    .sortedBy { it.price / it.shopStackingAmount }

            sell.forEach { shopChest ->
                message = message.append(sendShopInfo(sender, shopChest))
                message = message.append(Component.text("\n"))
                sum++
            }
        }

        if (!disableBuy){
            val buy =
                shop.filter { it.shopType == ShopType.BUYING && (withContext(Dispatchers.minecraft) { it.remainingSpace } > 0 || it.isUnlimited) }
                    .sortedBy { -it.price / it.shopStackingAmount }

            buy.forEach { shopChest ->
                message = message.append(sendShopInfo(sender, shopChest))
                message = message.append(Component.text("\n"))
                sum++
            }
        }


        sender.sendRichMessage("<color:green>検索結果: ${sum}件")
        sender.sendMessage(message)
    }

    private suspend fun sendShopInfo(sender: CommandSender, shopChest: Shop): Component {
        val nearPlace = getNearPlace(shopChest)
        val nearTownDistance = hypot(
            nearPlace!!.x.toDouble() - shopChest.location.blockX, nearPlace.z.toDouble() - shopChest.location.blockZ
        )
        val playerLocation =
            if (sender is Player) sender.location else Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)
        val distance = getPlayerDistance(playerLocation, shopChest)
        val count = if (shopChest.isBuying) getBuyingShopCount(shopChest) else getSellingShopCount(shopChest)

        val enchantmentList =
            shopChest.item.enchantments.map { "<lang:${it.key.translationKey()}> <lang:enchantment.level.${it.value}>" }

        val enchantment = if (enchantmentList.isEmpty()) {
            ""
        } else {
            "<hover:show_text:'${enchantmentList.joinToString("\n")}'>エンチャント</hover>"
        }

        val mm = MiniMessage.miniMessage()
        val tags = arrayOf(
            Placeholder.component(
                "player-name", if (shopChest.isUnlimited) {
                    Component.text("アドミンショップ")
                } else {
                    Component.text(Bukkit.getOfflinePlayer(shopChest.owner).name.toString())
                }
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
            Placeholder.component(
                "shop-type", mm.deserialize(if (shopChest.isBuying) "<color:red>買取" else "<color:green>販売")
            ),
            Placeholder.component("enchantment", mm.deserialize(enchantment))
        )

        return mm.deserialize(Config.config.format, *tags)
    }


    companion object {
        suspend fun getSellingShopCount(shopChest: Shop) = withContext(Dispatchers.minecraft) {
            if (shopChest.isUnlimited) {
                "無制限"
            } else {
                "${shopChest.remainingStock} * ${shopChest.shopStackingAmount}個"
            }
        }


        fun getPlayerDistance(playerLocation: Location, shopChest: Shop) =
            hypot(playerLocation.x - shopChest.location.x, playerLocation.z - shopChest.location.z).toInt()

        suspend fun getBuyingShopCount(shopChest: Shop) = withContext(Dispatchers.minecraft) {
            if (shopChest.isUnlimited) {
                "無制限"
            } else {
                "${shopChest.remainingSpace} * ${shopChest.shopStackingAmount}個"
            }
        }
    }


    fun <K, V> getKey(map: Map<K, V>, value: V): K? {
        for (key in map.keys) {
            if (value == map[key]) {
                return key
            }
        }
        return null
    }
}

fun getNearPlace(shopChest: Shop) = Config.config.placeData.minByOrNull {
    hypot(it.x.toDouble() - shopChest.location.blockX, it.z.toDouble() - shopChest.location.blockZ)
}
