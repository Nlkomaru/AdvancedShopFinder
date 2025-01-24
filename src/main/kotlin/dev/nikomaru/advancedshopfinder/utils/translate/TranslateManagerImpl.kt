package dev.nikomaru.advancedshopfinder.utils.translate

import dev.nikomaru.advancedshopfinder.utils.translate.serializer.NamespacedKeySerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.bukkit.NamespacedKey
import java.io.File
import java.io.InputStream
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
class TranslateManagerImpl : TranslateManager {
    var translateData: TranslateData

    private var langList: ArrayList<Locale> = arrayListOf()
    private val json = Json {
        prettyPrint = true
        isLenient = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    override fun getTranslateMap(locale: Locale): Map<NamespacedKey, String> {

        if (!langList.contains(locale)) {
            println("Supported Lang :${langList.joinToString(", ")}",)
            throw IllegalArgumentException("Locale not found")
        }
        translateData.data[locale]?.let {
            return it
        }
        val classLoader = this.javaClass.classLoader
        val inputStream: InputStream = classLoader.getResourceAsStream("minecraft/${locale.toLanguageTag()}.json")!!
        val translateMap: Map<@Serializable(with = NamespacedKeySerializer::class) NamespacedKey, String> = json.decodeFromStream(inputStream)
        translateData.data += (locale to translateMap)
        return translateMap
    }

    init {
        val resourceDir = "minecraft"
        val classLoader = this.javaClass.classLoader
        val resourcePath = classLoader.getResource(resourceDir)?.path ?: throw IllegalArgumentException("Resource directory not found")
        val files = File(resourcePath).listFiles().toList()


        files.forEach {
            val locale = Locale.of(it.nameWithoutExtension)
            langList.add(locale)
        }

        val dataMap = mutableMapOf<Locale, Map<NamespacedKey, String>>()

        val preloadLang = listOf<String>("en_us", "ja_jp")

        for (lang in preloadLang) {
            val locale = Locale.of(lang)
            val inputStream: InputStream = classLoader.getResourceAsStream("$resourceDir/${lang}.json")!!
            val translateMap: Map<NamespacedKey, String> = json.decodeFromStream(MapSerializer(NamespacedKeySerializer, String.serializer()), inputStream)
            dataMap[locale] = translateMap
        }

        translateData = TranslateData(dataMap)
    }
}