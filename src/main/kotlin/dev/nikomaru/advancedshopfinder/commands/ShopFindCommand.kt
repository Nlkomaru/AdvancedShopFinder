package dev.nikomaru.advancedshopfinder.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import com.ghostchu.quickshop.api.shop.ShopType
import dev.nikomaru.advancedshopfinder.AdvancedShopFinder
import dev.nikomaru.advancedshopfinder.files.Config
import dev.nikomaru.advancedshopfinder.utils.coroutines.minecraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.text.MessageFormat
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
        val message = arrayListOf<String>()
        sender.sendRichMessage("<color:green>検索結果: ${shop.size}件")
        val sell = shop.filter { it.shopType == ShopType.SELLING && withContext(Dispatchers.minecraft){it.remainingStock} > 0 }.sortedBy { it.price }
        sell.forEach { shopChest ->
            val nearPlace = Config.config.placeData.minByOrNull {
                hypot(it.x.toDouble() - shopChest.location.blockX, it.z.toDouble() - shopChest.location.blockZ)
            }
            val nearTownDistance = hypot(
                nearPlace!!.x.toDouble() - shopChest.location.blockX, nearPlace.z.toDouble() - shopChest.location.blockZ
            )
            val playerLocation =
                if (sender is Player) sender.location else Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)
            val distance =
                hypot(playerLocation.x - shopChest.location.x, playerLocation.z - shopChest.location.z).toInt()
            val count = withContext(Dispatchers.minecraft) {
                if (shopChest.isUnlimited) {
                    "無制限"
                } else {
                    "${shopChest.remainingStock} * ${shopChest.shopStackingAmount}個"
                }
            }
            message.add(
                MessageFormat.format(
                    MessageFormat.format(
                        Config.config.format,
                        Bukkit.getOfflinePlayer(shopChest.owner).name,
                        shopChest.price,
                        shopChest.shopStackingAmount,
                        count,
                        shopChest.location.world.name,
                        shopChest.location.blockX,
                        shopChest.location.blockY,
                        shopChest.location.blockZ,
                        distance,
                        nearPlace.placeName,
                        nearTownDistance.toInt(),
                        "<color:green>販売"
                    )
                )
            )

        }
        val buy = shop.filter { it.shopType == ShopType.BUYING && withContext(Dispatchers.minecraft){it.remainingSpace} > 0 }.sortedBy { -it.price }
        buy.forEach { shopChest ->

            val nearPlace = Config.config.placeData.minByOrNull {
                hypot(it.x.toDouble() - shopChest.location.blockX, it.z.toDouble() - shopChest.location.blockZ)
            }
            val nearTownDistance = hypot(
                nearPlace!!.x.toDouble() - shopChest.location.blockX, nearPlace.z.toDouble() - shopChest.location.blockZ
            )
            val playerLocation =
                if (sender is Player) sender.location else Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)
            val distance =
                hypot(playerLocation.x - shopChest.location.x, playerLocation.z - shopChest.location.z).toInt()
            val count = withContext(Dispatchers.minecraft) {
                if (shopChest.isUnlimited) {
                    "無制限"
                } else {
                    "${shopChest.remainingSpace} * ${shopChest.shopStackingAmount}個"
                }
            }
            message.add(
                MessageFormat.format(
                    MessageFormat.format(
                        Config.config.format,
                        Bukkit.getOfflinePlayer(shopChest.owner).name,
                        shopChest.price,
                        shopChest.shopStackingAmount,
                        count,
                        shopChest.location.world.name,
                        shopChest.location.blockX,
                        shopChest.location.blockY,
                        shopChest.location.blockZ,
                        distance,
                        nearPlace.placeName,
                        nearTownDistance.toInt(),
                        "<color:red>買取"
                    )
                )
            )
        }

        sender.sendRichMessage(message.joinToString("\n"))
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