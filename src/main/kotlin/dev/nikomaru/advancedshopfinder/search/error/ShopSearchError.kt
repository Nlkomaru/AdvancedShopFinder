package dev.nikomaru.advancedshopfinder.search.error

enum class ShopSearchError(
    val message: String
) {
    NO_SHOP_FOUND("指定されたショップが見つかりませんでした"),
    LIMIT_EXCEEDE("検索結果が多すぎます")
}