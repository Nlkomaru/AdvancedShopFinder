package dev.nikomaru.advancedshopfinder.utils.translate

import org.bukkit.NamespacedKey
import org.junit.jupiter.api.Test
import java.util.Locale

class TranslateManagerImplTest {

    @Test
    fun testTranslate() {
        val translateManager: TranslateManager = TranslateManagerImpl()
        translateManager.getTranslateMap(Locale.of("ja_JP")).entries.take(5).forEach {
            println(it.key)
            println(it.value)
        }
    }

    @Test
    fun getNamespacedKey() {
        val target = "block.minecraft.acacia_button"
        val namespaced = NamespacedKey.minecraft(target)
        println(namespaced.key)
    }
}

