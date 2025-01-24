package dev.nikomaru.advancedshopfinder.utils.translate

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import org.bukkit.NamespacedKey

import java.util.Locale

data class TranslateData(
    val data: MutableMap<Locale, Map<NamespacedKey, String>>
)