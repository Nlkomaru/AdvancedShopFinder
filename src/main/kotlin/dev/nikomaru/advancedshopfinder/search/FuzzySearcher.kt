package dev.nikomaru.advancedshopfinder.search

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.ghostchu.quickshop.api.QuickShopAPI
import dev.nikomaru.advancedshopfinder.search.error.ShopSearchError
import dev.nikomaru.advancedshopfinder.utils.translate.TranslateManager
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject
import java.util.Locale

class FuzzySearcher : Searcher<Pair<String, Locale>> {
    val quickShopAPI: QuickShopAPI by inject()
    val translateManager: TranslateManager by inject()

    override fun search(query: Pair<String, Locale>): Either<ShopSearchError, ArrayList<ItemStack>> {
        val needle = query.first.lowercase()
        val matchedKeys = translateManager.getTranslateMap(query.second)
            .filter { (k, v) ->
                v.lowercase().contains(needle) || k.key.lowercase().contains(needle)
            }
            .keys

        if (matchedKeys.isEmpty()) return ShopSearchError.NO_SHOP_FOUND.left()

        val shops = quickShopAPI.shopManager.allShops.filter { it.item.type.key in matchedKeys }

        if (shops.isEmpty()) return ShopSearchError.NO_SHOP_FOUND.left()
        return shops.map { it.item }.toCollection(ArrayList()).right()
    }
}