# 別の言語を追加するには？

以下の手順に従って、Minecraftの言語ファイルを取得し、翻訳ファイルを作成します。

```kotlin
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun main() {
    val base = ".minecraft/assets/" //TODO REPLACE TO YOUR ASSETS FOLDER
    
    val indexPath = base + "indexes/<version>.json" //TODO REPLACE TO YOUR INDEX FILE
    val lang = arrayListOf("ja_JP", "ko_KR") //TODO REPLACE TO YOUR LANGUAGES
    val indexFile = File(indexPath)
    val index = indexFile.readText()
    val indexJson = Json.decodeFromString<Index>(index)

    for (l in lang) {
        val hash = indexJson.objects["minecraft/lang/${l.lowercase()}.json"]!!.hash
        val path = base + "objects/${hash.substring(0, 2)}/$hash"
        val file = File(path)
        val text = file.readText()

        val gson = com.google.gson.Gson()
        val map = gson.fromJson(text, Map::class.java)

        val translateMap = mutableMapOf<String, String>()
        map.filter { (t, _) -> t.toString().startsWith("item.") || t.toString().startsWith("block.") }
            .forEach { (t, u) ->
                translateMap += t.toString() to u.toString()
                println("$t : $u")
            }
        val translateMap2 = TranslateMap(translateMap)
        val json = Json.encodeToString(translateMap2)

        val output = File("./lang/$l.json") // if you want to change the output folder, replace the path
        output.parentFile.mkdirs()
        output.createNewFile()
        output.writeText(json)
    }
}

@kotlinx.serialization.Serializable
data class TranslateMap(
    var map: Map<String, String>
)

@kotlinx.serialization.Serializable
data class Index(
    var objects: Map<String, IndexObject>
)

@kotlinx.serialization.Serializable
data class IndexObject(
    var hash: String,
    var size: Int
)
```




