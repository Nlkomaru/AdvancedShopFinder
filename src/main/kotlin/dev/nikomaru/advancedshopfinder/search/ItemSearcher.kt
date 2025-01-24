package dev.nikomaru.advancedshopfinder.search

import arrow.core.Either
import dev.nikomaru.advancedshopfinder.search.error.ShopSearchError
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemSearcher : Searcher<Array<Material>> {
    override fun search(query: Array<Material>): Either<ShopSearchError, ArrayList<ItemStack>> {
        TODO()
    }
}