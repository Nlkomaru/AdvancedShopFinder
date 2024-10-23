package dev.nikomaru.advancedshopfinder.commands.utils.parser

import dev.nikomaru.advancedshopfinder.files.server.TranslateMap
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.context.CommandInput
import org.incendo.cloud.parser.ArgumentParseResult
import org.incendo.cloud.parser.ArgumentParser
import org.incendo.cloud.parser.ParserDescriptor
import org.incendo.cloud.suggestion.BlockingSuggestionProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.collections.mapNotNull

class MaterialArrayParser<CommandSender> : ArgumentParser<CommandSender, Array<Material>>,
    BlockingSuggestionProvider.Strings<CommandSender>, KoinComponent {
    private val translateData: TranslateMap by inject()

    companion object {
        fun materialArrayParser(): ParserDescriptor<CommandSender, Array<Material>> {
            return ParserDescriptor.of(MaterialArrayParser(), Array<Material>::class.java)
        }
    }

    override fun parse(
        commandContext: CommandContext<CommandSender & Any>, commandInput: CommandInput
    ): ArgumentParseResult<Array<Material>> {
        val input = commandInput.readString()
        val item = getKeys(translateData.map, input)?.map { keys ->  Material.entries.find { it.translationKey() == keys } }
            ?: listOf(Material.matchMaterial(input))
        val res = item.filter { it != null }.map { it!! }.toTypedArray()
        return ArgumentParseResult.success(res)
    }

    override fun stringSuggestions(
        commandContext: CommandContext<CommandSender>, input: CommandInput
    ): MutableIterable<String> {
        return (Material.entries.mapNotNull {
            translateData.map[it.translationKey()]
        } + Material.entries.map { it.name.lowercase() }).toMutableList()
    }

    private fun <K, V> getKeys(map: Map<K, V>, value: V): List<K>? {
        val list = arrayListOf<K>()
        for (key in map.keys) {
            if (value == map[key]) {
                list.add(key)
            }
        }
        return if (list.isEmpty()) {
            null
        } else {
            list
        }
    }


}