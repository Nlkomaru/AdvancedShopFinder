package dev.nikomaru.advancedshopfinder.files

import kotlinx.serialization.Serializable

@Serializable
data class TranslateMap(
    var map: Map<String, String>
)