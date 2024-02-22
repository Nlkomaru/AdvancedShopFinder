package dev.nikomaru.advancedshopfinder.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

object ComponentUtils {
    fun Component.toPlainText(): String {
        return PlainTextComponentSerializer.plainText().serialize(this)
    }

    fun Component.toLegacyText(): String {
        return LegacyComponentSerializer.builder().build().serialize(this)
    }

    fun Component.toGsonText(): String {
        return GsonComponentSerializer.gson().serialize(this)
    }

    fun String.toComponent(): Component {
        return MiniMessage.miniMessage().deserialize(this)
    }

    fun Component.toMiniMessage(): String {
        return MiniMessage.miniMessage().serialize(this)
    }

}