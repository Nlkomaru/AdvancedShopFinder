package dev.nikomaru.advancedshopfinder.search

import arrow.core.Either
import dev.nikomaru.advancedshopfinder.search.error.ShopSearchError
import org.bukkit.inventory.ItemStack
import org.koin.core.component.KoinComponent

interface Searcher<T> : KoinComponent {
    fun search(query: T): Either<ShopSearchError, ArrayList<ItemStack>>
}