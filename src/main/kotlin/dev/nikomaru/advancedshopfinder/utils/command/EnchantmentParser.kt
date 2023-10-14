package dev.nikomaru.advancedshopfinder.utils.command


import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import revxrsal.commands.bukkit.BukkitCommandHandler
import revxrsal.commands.command.CommandActor
import revxrsal.commands.command.ExecutableCommand
import revxrsal.commands.process.ValueResolver

object EnchantmentParser : ValueResolver<Enchantment> {
    private fun suggestions(): Set<String> {
        return Enchantment.values().map { it.key.key }.toSet()
    }

    override fun resolve(context: ValueResolver.ValueResolverContext): Enchantment {
        val enchantment = context.arguments().pop()?.let { Enchantment.getByKey(NamespacedKey.minecraft(it)) }
        return enchantment ?: throw IllegalArgumentException("Enchantment not found")
    }

    fun BukkitCommandHandler.enchantmentSupport() {
        val handler = this
        handler.autoCompleter.registerParameterSuggestions(
            Enchantment::class.java,
        ) { _: List<String>, _: CommandActor, _: ExecutableCommand ->
            suggestions()
        }
        handler.registerValueResolver(Enchantment::class.java, this@EnchantmentParser)
    }
}