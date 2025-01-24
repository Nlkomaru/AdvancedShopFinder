package dev.nikomaru.advancedshopfinder.utils.data

import kotlinx.serialization.Serializable

@Serializable
data class FindOption(
    val version: Int = 1,
    val limitAmountOption: LimitAmountOption = LimitAmountOption(),
    val sortOption: SortOption = SortOption(),
    val showNoStockShop: Boolean = false,
)

@Serializable
data class LimitAmountOption(
    val buyFindLimit: Int = -1,
    val sellFindLimit: Int = -1,
)

@Serializable
data class SortOption(
    val buySortType: SortType = SortType.DESC_PRICE_PER_ITEM,
    val sellSortType: SortType = SortType.ASC_PRICE_PER_ITEM,
)