package dev.nikomaru.advancedshopfinder.files.server

import kotlinx.serialization.Serializable

@Serializable
data class TranslateMap(
    var map: Map<String, String>
)