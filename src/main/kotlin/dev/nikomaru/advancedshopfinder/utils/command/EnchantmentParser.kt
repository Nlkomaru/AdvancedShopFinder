package dev.nikomaru.advancedshopfinder.utils.command

import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.context.CommandContext
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import java.util.*

class EnchantmentParser<C> : ArgumentParser<C, Enchantment> {

    override fun parse(
        commandContext: CommandContext<C & Any>,
        inputQueue: Queue<String>
    ): ArgumentParseResult<Enchantment> {
        try {
            val enchantment = inputQueue.poll()?.let { Enchantment.getByKey(NamespacedKey.minecraft(it)) }
                ?: return ArgumentParseResult.failure(IllegalArgumentException("enchantment is null"))
            return ArgumentParseResult.success(enchantment)
        } catch (e: Exception) {
            return ArgumentParseResult.failure(e)
        }
    }

    override fun suggestions(commandContext: CommandContext<C>, input: String): MutableList<String> {
        return Enchantment.values().map { it.key.key }.toMutableList()
    }
}