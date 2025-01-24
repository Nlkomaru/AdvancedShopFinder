package dev.nikomaru.advancedshopfinder.search

import arrow.core.Either
import arrow.core.right
import com.ghostchu.quickshop.api.QuickShopAPI
import dev.nikomaru.advancedshopfinder.search.error.ShopSearchError
import dev.nikomaru.advancedshopfinder.utils.translate.TranslateManager
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject
import java.util.Locale
import kotlin.collections.component1
import kotlin.collections.component2

class FuzzySearcher : Searcher<Pair<String, Locale>> {
    val quickShopAPI: QuickShopAPI by inject()
    val translateManager: TranslateManager by inject()

    override fun search(query: Pair<String, Locale>): Either<ShopSearchError, ArrayList<ItemStack>> {
        translateManager.getTranslateMap(query.second).filter { (k, v) ->
            v.contains(query.first) || k.key.contains(query.first)
        }.map {
            it.key
        }

        TODO()
    }
}