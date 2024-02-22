package dev.nikomaru.advancedshopfinder.utils.command

import org.bukkit.Material
import revxrsal.commands.bukkit.BukkitCommandHandler
import revxrsal.commands.command.CommandActor
import revxrsal.commands.command.ExecutableCommand
import revxrsal.commands.process.ValueResolver

object MaterialParser: ValueParser<Material>() {
    override fun suggestions(args: List<String>, sender: CommandActor, command: ExecutableCommand): Set<String> {
        return Material.entries.map { it.name }.toSet()
    }

    override fun resolve(context: ValueResolver.ValueResolverContext): Material {
        val material = context.arguments().pop()?.let { Material.getMaterial(it) }
        return material ?: throw IllegalArgumentException("Material not found")
    }

    fun BukkitCommandHandler.materialSupport() {
        val handler = this
        handler.autoCompleter.registerParameterSuggestions(
            Material::class.java,
        ) { args: List<String>, sender: CommandActor, command: ExecutableCommand ->
            suggestions(args, sender, command)
        }
        handler.registerValueResolver(Material::class.java, this@MaterialParser)
    }
}