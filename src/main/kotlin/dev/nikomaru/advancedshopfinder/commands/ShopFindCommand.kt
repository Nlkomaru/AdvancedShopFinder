package dev.nikomaru.advancedshopfinder.commands

import com.ghostchu.quickshop.api.QuickShopAPI
import com.ghostchu.quickshop.api.shop.Shop
import com.ghostchu.quickshop.api.shop.ShopType
import com.github.shynixn.mccoroutine.bukkit.launch
import dev.nikomaru.advancedshopfinder.AdvancedShopFinder
import dev.nikomaru.advancedshopfinder.data.FindOption
import dev.nikomaru.advancedshopfinder.data.SortType
import dev.nikomaru.advancedshopfinder.files.ConfigData
import dev.nikomaru.advancedshopfinder.files.PlaceData
import dev.nikomaru.advancedshopfinder.files.TranslateMap
import dev.nikomaru.advancedshopfinder.utils.command.ItemNameSuggestion
import dev.nikomaru.advancedshopfinder.utils.coroutines.async
import dev.nikomaru.advancedshopfinder.utils.coroutines.minecraft
import dev.nikomaru.advancedshopfinder.utils.data.PlayerFindOptionUtils.getPlayerFindOption
import dev.nikomaru.advancedshopfinder.utils.display.LuminescenceShulker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Description
import revxrsal.commands.annotation.Subcommand
import java.util.function.UnaryOperator
import kotlin.math.hypot


@Command("advancedshopfinder", "asf", "shopfinder", "sf")
object ShopFindCommand: KoinComponent {
    private val translateData: TranslateMap by inject()
    private val quickShop: QuickShopAPI by inject()
    private val plugin: AdvancedShopFinder by inject()
    private val config: ConfigData by inject()

    @Subcommand("search")
    @Description("アイテムを検索します")
    suspend fun searchItem(
        sender: CommandSender, @ItemNameSuggestion itemName: String
    ) {
        val item = getKey(translateData.map, itemName) ?: Material.matchMaterial(itemName)?.translationKey()
        val options = (sender as? Player)?.getPlayerFindOption() ?: FindOption()
        val shop = quickShop.shopManager.allShops.filter {
            it.item.type.translationKey().equals(item, true)
        }

        if (shop.isEmpty()) {
            sender.sendRichMessage("検索結果: 0件")
            return
        }
        var message: Component = Component.text("")
        var sum = 0

        val (newSellMessage, newSellSum) = processShops(
            shop, sender, message, sum, options, ShopType.SELLING
        )
        message = newSellMessage
        sum = newSellSum


        val (newBuyMessage, newBuySum) = processShops(
            shop, sender, message, sum, options, ShopType.BUYING
        )
        message = newBuyMessage
        sum = newBuySum

        sender.sendRichMessage("<color:green><lang:${item}> の検索結果: ${sum}件")
        sender.sendMessage(message)
    }

    suspend fun processShops(
        shop: List<Shop>, sender: CommandSender, message: Component, sum: Int, options: FindOption, shopType: ShopType
    ): Pair<Component, Int> {
        var filteredShops = shop.filter {
            it.shopType == shopType && (withContext(Dispatchers.minecraft) {
                if (shopType == ShopType.SELLING) it.remainingStock else it.remainingSpace
            } > 0 || it.isUnlimited)
        }
        val sortType =
            if (shopType == ShopType.SELLING) options.sortOption.sellSortType else options.sortOption.buySortType
        if (sender !is Player) {
            filteredShops = if (shopType == ShopType.SELLING) filteredShops.sortedByDescending { it.price }
            else filteredShops.sortedBy { it.price }
        } else {
            filteredShops = when (sortType) {
                SortType.ASC_PRICE_PER_STACK -> filteredShops.sortedBy { it.price }
                SortType.DESC_PRICE_PER_STACK -> filteredShops.sortedByDescending { it.price }
                SortType.ASC_PRICE_PER_ITEM -> filteredShops.sortedBy { it.price / it.shopStackingAmount }
                SortType.DESC_PRICE_PER_ITEM -> filteredShops.sortedByDescending { it.price / it.shopStackingAmount }
                SortType.ASC_DISTANCE -> filteredShops.sortedBy { getPlayerDistance(sender.location, it) }
                SortType.DESC_DISTANCE -> filteredShops.sortedByDescending { getPlayerDistance(sender.location, it) }
                SortType.ASC_DISTANCE_NEAREST -> filteredShops.sortedBy { getNearestPlaceDistance(it) }
                SortType.DESC_DISTANCE_NEAREST -> filteredShops.sortedByDescending {
                    getNearestPlaceDistance(it)
                }
            }
        }
        var newMessage = message
        var newSum = sum

        filteredShops.forEach { shopChest ->
            newMessage = newMessage.append(sendShopInfo(sender, shopChest))
            newMessage = newMessage.append(Component.text("\n"))
            newSum++
        }

        if (options.lightningOption.lightning && sender is Player) {
            plugin.launch {
                async(Dispatchers.async) {
                    val luminescenceShulker = LuminescenceShulker()
                    luminescenceShulker.addTarget(sender)
                    filteredShops.stream()
                        .filter { getPlayerDistance(sender.location, it) < options.lightningOption.lightningDistance }
                        .forEach {
                            luminescenceShulker.addBlock(it.location)
                        }
                    repeat(options.lightningOption.lightningCount) {
                        luminescenceShulker.display()
                        delay(options.lightningOption.lightningTimeMills)
                        luminescenceShulker.stop()
                        delay(options.lightningOption.lightningIntervalMills)
                    }
                }
            }
        }

        return Pair(newMessage, newSum)
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
        val tags = getTags(shopChest, count, distance, nearPlace, nearTownDistance)
        val item = shopChest.item
        val mm = MiniMessage.miniMessage()
        val message = mm.deserialize(config.format, *tags)
        val hoverMessage = Bukkit.getItemFactory().asHoverEvent(item, UnaryOperator.identity()).value()
        message.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_ITEM, hoverMessage))

        return mm.deserialize(
            "<hover:show_item:${item.type.name.lowercase()}:${item.amount}:'${item.itemMeta.asString}'>${
                mm.serialize(message)
            }"
        )
    }


    private fun getNearestPlaceDistance(shopChest: Shop) = hypot(
        getNearPlace(shopChest)!!.x.toDouble() - shopChest.location.blockX,
        getNearPlace(shopChest)!!.z.toDouble() - shopChest.location.blockZ
    )

    private fun getTags(
        shopChest: Shop, count: String, distance: Int, nearPlace: PlaceData, nearTownDistance: Double
    ): Array<TagResolver.Single> {
        val mm = MiniMessage.miniMessage()

        return arrayOf(
            Placeholder.component(
                "player-name", if (shopChest.isUnlimited) {
                    Component.text("アドミンショップ")
                } else {
                    Component.text(Bukkit.getOfflinePlayer(shopChest.owner.uniqueId!!).name.toString())
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
            )
        )
    }

    private suspend fun getSellingShopCount(shopChest: Shop) = withContext(Dispatchers.minecraft) {
        if (shopChest.isUnlimited) {
            "無制限"
        } else {
            "${shopChest.remainingStock} * ${shopChest.shopStackingAmount}個"
        }
    }


    private fun getPlayerDistance(playerLocation: Location, shopChest: Shop) =
        hypot(playerLocation.x - shopChest.location.x, playerLocation.z - shopChest.location.z).toInt()

    private suspend fun getBuyingShopCount(shopChest: Shop) = withContext(Dispatchers.minecraft) {
        if (shopChest.isUnlimited) {
            "無制限"
        } else {
            "${shopChest.remainingSpace} * ${shopChest.shopStackingAmount}個"
        }
    }


    private fun <K, V> getKey(map: Map<K, V>, value: V): K? {
        for (key in map.keys) {
            if (value == map[key]) {
                return key
            }
        }
        return null
    }


    private fun getNearPlace(shopChest: Shop) = config.placeData.minByOrNull {
        hypot(it.x.toDouble() - shopChest.location.blockX, it.z.toDouble() - shopChest.location.blockZ)
    }
}
