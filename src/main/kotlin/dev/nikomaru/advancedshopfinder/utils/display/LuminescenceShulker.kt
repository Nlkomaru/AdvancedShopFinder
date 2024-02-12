package dev.nikomaru.advancedshopfinder.utils.display

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject
import dev.nikomaru.advancedshopfinder.utils.coroutines.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import kotlin.random.Random

class LuminescenceShulker: KoinComponent {
    val protocolManager: ProtocolManager by inject()

    private val ids = arrayListOf<Int>()

    private val blocks = arrayListOf<Location>()

    private val target = arrayListOf<Player>()

    fun addBlock(location: Location) {
        blocks.add(location)
    }

    fun addTarget(player: Player) {
        target.add(player)
    }

    suspend fun display() {
        withContext(Dispatchers.async) {
            blocks.forEach { location ->
                target.forEach {
                    val entityId = Random.nextInt(Int.MAX_VALUE)
                    ids.add(entityId)

                    val shulkerPacket = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY)
                    shulkerPacket.integers.write(0, entityId)
                    shulkerPacket.entityTypeModifier.write(0, EntityType.SHULKER)
                    shulkerPacket.uuiDs.write(0, UUID.randomUUID())
                    shulkerPacket.doubles.write(0, location.x).write(1, location.y).write(2, location.z)

                    val byteSerializer = WrappedDataWatcher.Registry.get(java.lang.Byte::class.java)

                    val shulkerEffectPacket = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
                    shulkerEffectPacket.integers.write(0, entityId)
                    val watcher = WrappedDataWatcher()
                    watcher.setObject(
                        WrappedDataWatcherObject(0, byteSerializer), 0x60.toByte()
                    )
                    val wrappedDataValueList: MutableList<WrappedDataValue> = arrayListOf()
                    watcher.watchableObjects.stream().filter(Objects::nonNull).forEach { entry ->
                        val dataWatcherObject: WrappedDataWatcherObject = entry.watcherObject
                        wrappedDataValueList.add(
                            WrappedDataValue(
                                dataWatcherObject.index, dataWatcherObject.serializer, entry.rawValue
                            )
                        )
                    }
                    shulkerEffectPacket.dataValueCollectionModifier.write(0, wrappedDataValueList)

                    protocolManager.sendServerPacket(it, shulkerPacket)
                    protocolManager.sendServerPacket(it, shulkerEffectPacket)
                }
            }
        }
    }

    suspend fun stop() {
        withContext(Dispatchers.async) {
            target.forEach {
                val shulkerDeadPacket = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
                shulkerDeadPacket.intLists.write(0, ids)
                protocolManager.sendServerPacket(it, shulkerDeadPacket)
            }
            ids.clear()
        }
    }
}