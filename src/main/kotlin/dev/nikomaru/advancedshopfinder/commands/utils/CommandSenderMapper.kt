import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.incendo.cloud.SenderMapper

@Suppress("UnstableApiUsage")
class CommandSenderMapper : SenderMapper<CommandSourceStack, CommandSender> {
    override fun map(source: CommandSourceStack): CommandSender {
        return source.sender
    }

    override fun reverse(sender: CommandSender): CommandSourceStack {
        return object : CommandSourceStack {
            override fun getLocation(): Location {
                if (sender is Entity) {
                    return sender.location
                }
                val worlds = Bukkit.getWorlds()
                return Location(if (worlds.isEmpty()) null else worlds.first(), 0.0, 0.0, 0.0)
            }



            override fun getSender(): CommandSender {
                return sender
            }

            override fun getExecutor(): Entity? {
                return if (sender is Entity) sender else null
            }


            //TODO check later
            override fun withLocation(p0: Location): CommandSourceStack {
                return sender as CommandSourceStack
            }

            override fun withExecutor(p0: Entity): CommandSourceStack {
                return sender as CommandSourceStack
            }
        }
    }
}