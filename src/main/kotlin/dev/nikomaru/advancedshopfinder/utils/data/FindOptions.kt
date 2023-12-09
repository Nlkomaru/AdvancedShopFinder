package dev.nikomaru.advancedshopfinder.utils.data

data class FindOptions (
    val sortType: SortType,
    val disablingBuy: Boolean,
    val disablingSell: Boolean,
    val disablingLighting: Boolean,
    val lightningTime: Long,
    val lightningInterval: Long,
    val lightningCount: Int,
    val lightningDistance: Int,
)

enum class SortType {
    ASC_PRICE_PER_STACK,
    DESC_PRICE_PER_STACK,
    ASC_PRICE_PER_ITEM,
    DESC_PRICE_PER_ITEM,
    ASC_DISTANCE,
    DESC_DISTANCE,
    ASC_DISTANCE_NEAREST,
    DESC_DISTANCE_NEAREST,
}