package com.example.impostorx.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object WordsRepository {

    enum class Category(val raw: String) {
        Random("Random"),
        Futbol("Futbol"),
        Deportes("Deportes"),
        Artistas("Artistas"),
        Geografia("Geografia"),
        Cine("Cine")
    }

    private fun fileFor(category: Category): String = when (category) {
        Category.Futbol     -> "palabras/futbol.json"
        Category.Deportes   -> "palabras/deportistas.json"
        Category.Artistas   -> "palabras/artistas.json"
        Category.Geografia  -> "palabras/geografia.json"
        Category.Cine       -> "palabras/peliculas.json"
        Category.Random     -> "palabras/random.json"
    }

    fun loadWords(context: Context, category: Category): List<String> {
        // Intenta leer el archivo esperado
        val primary = runCatching {
            context.assets.open(fileFor(category)).use { it.readBytes().toString(Charsets.UTF_8) }
        }.getOrNull()

        if (primary != null) return parseWords(primary)

        // Fallback para "Random" si no existiera el json
        if (category == Category.Random) {
            val files = listOf(
                "palabras/futbolistas.json",
                "palabras/deportistas.json",
                "palabras/artistas.json",
                "palabras/paises.json",
                "palabras/peliculas.json"
            )
            val all = mutableListOf<String>()
            files.forEach { f ->
                runCatching {
                    context.assets.open(f).use { it.readBytes().toString(Charsets.UTF_8) }
                }.onSuccess { json ->
                    all += parseWords(json)
                }
            }
            return all.distinct()
        }

        return emptyList()
    }

    // Soporta:
    //  - ["Messi","Ronaldo", ...]
    //  - [{"nombre":"Messi"}, {"nombre":"Ronaldo"}]
    //  - {"palabras":[...]} o {"nombres":[...]} o {"items":[...]}
    private fun parseWords(json: String): List<String> {
        fun JSONArray.toListStringsOrNombre(): List<String> =
            (0 until length()).mapNotNull { i ->
                val v = opt(i)
                when (v) {
                    is String -> v.trim().takeIf { it.isNotEmpty() }
                    is JSONObject -> v.optString("nombre").takeIf { it.isNotEmpty() }
                    else -> null
                }
            }

        // 1) Top-level array
        runCatching { JSONArray(json).toListStringsOrNombre() }.getOrNull()?.let { return it }

        // 2) Top-level object con alguna key conocida
        runCatching {
            val obj = JSONObject(json)
            listOf("palabras", "nombres", "items", "data").firstNotNullOfOrNull { key ->
                if (obj.has(key)) obj.getJSONArray(key).toListStringsOrNombre() else null
            } ?: emptyList()
        }.getOrNull()?.let { return it }

        return emptyList()
    }
}
