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
import sun.rmi.server.Dispatcher
import kotlin.math.hypot


@CommandMethod("advancedshopfinder|asf|shopfinder|sf")
class ShopFindCommand {
    @CommandMethod("search <itemName>")
    suspend fun searchItem(
        sender: CommandSender,
        @Argument(value = "itemName", suggestions = "itemName") itemName: String,
    ) {
        val shop = AdvancedShopFinder.quickShop.shopManager.allShops.filter {
            it.item.type.name.equals(itemName, true)
        }
        if (shop.isEmpty()) {
            sender.sendRichMessage("検索結果: 0件")
            return
        }
        sender.sendRichMessage("<color:green>検索結果: ${shop.size}件")
        val sell = shop.filter { it.shopType == ShopType.SELLING }
        sell.forEach { shopChest ->
            val nearPlace = Config.config.placeData.minByOrNull {
                hypot(it.x.toDouble() - shopChest.location.blockX, it.z.toDouble() - shopChest.location.blockZ)
            }
            val distance = hypot(
                nearPlace!!.x.toDouble() - shopChest.location.blockX, nearPlace.z.toDouble() - shopChest.location.blockZ
            )
            val playerLocation =
                if (sender is Player) sender.location else Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)
            withContext(Dispatchers.minecraft) {
                sender.sendRichMessage(
                    "<color:green>販売: オーナー: <color:yellow>${Bukkit.getOfflinePlayer(shopChest.owner).name} " +
                    "<color:green>値段: <color:yellow>${shopChest.price}/${shopChest.shopStackingAmount}個 " +
                    "<color:green>在庫: <color:yellow>${
                        if (shopChest.isUnlimited) {
                            "無制限"
                        } else {
                            "${shopChest.remainingStock} * ${shopChest.shopStackingAmount}個"
                        }
                    } " +
                    "<color:green>座標: <color:yellow>${shopChest.location.world.name} x:${shopChest.location.blockX} y:${shopChest.location.blockY} z:${shopChest.location.blockZ} " +
                    "<color:green>距離: <color:yellow>${
                        hypot(
                            playerLocation.x - shopChest.location.x, playerLocation.z - shopChest.location.z
                        ).toInt()
                    }ブロック " + "<color:green>最寄り: <color:yellow>${nearPlace.placeName}から${distance.toInt()}ブロック"
                )
            }

        }
        val buy = shop.filter { it.shopType == ShopType.BUYING }
        buy.forEach { shopChest ->

            val nearPlace = Config.config.placeData.minByOrNull {
                hypot(it.x.toDouble() - shopChest.location.blockX, it.z.toDouble() - shopChest.location.blockZ)
            }
            val distance = hypot(
                nearPlace!!.x.toDouble() - shopChest.location.blockX, nearPlace.z.toDouble() - shopChest.location.blockZ
            )
            val playerLocation =
                if (sender is Player) sender.location else Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)

            withContext(Dispatchers.minecraft) {
                sender.sendRichMessage(
                    "<color:red>買取: オーナー: <color:yellow>${Bukkit.getOfflinePlayer(shopChest.owner).name} " +
                    "<color:green>値段: <color:yellow>${shopChest.price}/${shopChest.shopStackingAmount}個" +
                    "<color:green>在庫: <color:yellow>${
                        if (shopChest.isUnlimited) {
                            "無制限"
                        } else {
                            "${shopChest.remainingSpace} * ${shopChest.shopStackingAmount}個"
                        }} " +
                    "<color:green>座標: <color:yellow>${shopChest.location.world.name} x:${shopChest.location.blockX} y:${shopChest.location.blockY} z:${shopChest.location.blockZ} " +
                    "<color:green>距離: <color:yellow>${
                        hypot(
                            playerLocation.x - shopChest.location.x, playerLocation.z - shopChest.location.z
                        ).toInt()
                    }ブロック " +
                    "<color:green>最寄り: <color:yellow>${nearPlace.placeName}から${distance.toInt()}ブロック")
                    }
            }

        }

        @Suggestions("itemName")
        fun itemNameSuggestions(sender: CommandContext<CommandSender>, input: String?): List<String> {
            return Material.values().filter { it.isItem }.map { it.name.lowercase() }
        }
    }