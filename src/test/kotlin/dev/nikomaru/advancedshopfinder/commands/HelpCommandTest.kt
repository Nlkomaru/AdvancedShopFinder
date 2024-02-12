package dev.nikomaru.advancedshopfinder.commands

import be.seeseemelk.mockbukkit.ServerMock
import dev.nikomaru.advancedshopfinder.AdvancedShopFinderTest
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.test.KoinTest
import org.koin.test.inject

@ExtendWith(AdvancedShopFinderTest::class)
class HelpCommandTest: KoinTest {
    private val server: ServerMock by inject()

    @org.junit.jupiter.api.Test
    fun help() {
        val player = server.addPlayer()
        val res = player.performCommand("asf help")
        println(player.nextMessage())
        assert(res)
    }
}