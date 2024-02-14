package dev.nikomaru.advancedshopfinder.data

import kotlinx.serialization.Serializable

@Serializable
data class FindOption(
    val version: Int = 1,
    val limitAmountOption: LimitAmountOption = LimitAmountOption(),
    val sortOption: SortOption = SortOption(),
    val lightningOption: LightingOption = LightingOption(),
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

@Serializable
data class LightingOption(
    val lightning: Boolean = true,
    val lightningTimeMills: Long = 1000,
    val lightningIntervalMills: Long = 400,
    val lightningCount: Int = 10,
    val lightningDistance: Int = 200,
)