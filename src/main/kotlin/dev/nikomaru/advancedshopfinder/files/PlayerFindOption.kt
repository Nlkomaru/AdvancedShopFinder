package dev.nikomaru.advancedshopfinder.files

import dev.nikomaru.advancedshopfinder.data.FindOption
import kotlinx.serialization.Serializable

@Serializable
data class PlayerFindOption(
    val setting: String = "default",
    val findOptions: HashMap<String, FindOption> = hashMapOf("default" to FindOption()),
)


