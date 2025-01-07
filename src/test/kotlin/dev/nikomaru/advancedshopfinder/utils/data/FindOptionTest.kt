package dev.nikomaru.advancedshopfinder.utils.data

import dev.nikomaru.advancedshopfinder.files.server.Config
import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Test

class FindOptionTest {

    @Test
    fun generateFindOptionTest(){
        val findOption = FindOption()
        val json = Config.json.encodeToString(findOption)
        println(json)
        assert(true)
    }

}