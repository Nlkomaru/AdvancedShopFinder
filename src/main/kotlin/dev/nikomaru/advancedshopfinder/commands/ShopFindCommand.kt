package dev.nikomaru.advancedshopfinder.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import com.ghostchu.quickshop.api.shop.Shop
import com.ghostchu.quickshop.api.shop.ShopType
import dev.nikomaru.advancedshopfinder.AdvancedShopFinder
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
import org.bukkit.entity.Player
import kotlin.math.hypot


@CommandMethod("advancedshopfinder|asf|shopfinder|sf")
class ShopFindCommand {
    @CommandMethod("search <itemName>")
    suspend fun searchItem(
        sender: CommandSender,
        @Argument(value = "itemName", suggestions = "itemName") itemName: String,
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

        val sell =
            shop.filter { it.shopType == ShopType.SELLING && (withContext(Dispatchers.minecraft) { it.remainingStock } > 0 || it.isUnlimited) }
                .sortedBy { it.price / it.shopStackingAmount  }
        sell.forEach { shopChest ->
            val nearPlace = getNearPlace(shopChest)
            val nearTownDistance = hypot(
                nearPlace!!.x.toDouble() - shopChest.location.blockX, nearPlace.z.toDouble() - shopChest.location.blockZ
            )
            val playerLocation =
                if (sender is Player) sender.location else Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)
            val distance = getPlayerDistance(playerLocation, shopChest)
            val count = getBuyShopCount(shopChest)

            val enchantmentList =
                shopChest.item.enchantments.map { "<lang:${it.key.translationKey()}><lang:enchantment.level.${it.value}>" }
                    .joinToString("\n")
            val enchantment = "<hover:show_text:'$enchantmentList'>エンチャント</hover>"

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
                Placeholder.component("enchantment", mm.deserialize(enchantment))
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
            val count = getSellShopCount(shopChest)
            val enchantmentList =
                shopChest.item.enchantments.map { "<lang:${it.key.translationKey()}><lang:enchantment.level.${it.value}>" }
                    .joinToString("\n")
            val enchantment = "<hover:show_text:'$enchantmentList'><light_purple>エンチャント</light_purple></hover>"

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
                Placeholder.component("enchantment", mm.deserialize(enchantment))
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

    companion object{
        suspend fun getBuyShopCount(shopChest: Shop) = withContext(Dispatchers.minecraft) {
            if (shopChest.isUnlimited) {
                "無制限"
            } else {
                "${shopChest.remainingStock} * ${shopChest.shopStackingAmount}個"
            }
        }


        fun getPlayerDistance(playerLocation: Location, shopChest: Shop) =
            hypot(playerLocation.x - shopChest.location.x, playerLocation.z - shopChest.location.z).toInt()

        suspend fun getSellShopCount(shopChest: Shop) = withContext(Dispatchers.minecraft) {
            if (shopChest.isUnlimited) {
                "無制限"
            } else {
                "${shopChest.remainingSpace} * ${shopChest.shopStackingAmount}個"
            }
        }
    }


    @Suggestions("itemName")
    fun itemNameSuggestions(sender: CommandContext<CommandSender>, input: String?): List<String> {
        return Material.values().map {
            AdvancedShopFinder.translateData[it.translationKey()] ?: it.translationKey()
        } + Material.values().map { it.name.lowercase() }
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
