package com.example.impostorx.logic

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.impostorx.data.WordsRepository
import com.example.impostorx.data.WordsRepository.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class GameViewModel : ViewModel() {

    enum class Phase { ASSIGN, REVEAL }

    // Config
    private var players: List<String> = emptyList()
    private var category: Category = Category.Random
    private var impostors: Int = 1

    // Palabras
    private var seed: Long = System.currentTimeMillis()
    private var words: List<String> = emptyList()
    var roundWord by mutableStateOf<String?>(null)     // observable
        private set

    // Ronda
    var phase by mutableStateOf(Phase.ASSIGN)          // observable
        private set
    var currentIndex by mutableStateOf(0)              // observable
        private set
    private var impostorSet: Set<Int> = emptySet()

    fun setPlayers(list: List<String>) { players = list }
    fun selectCategory(cat: Category) { category = cat }
    fun setImpostors(n: Int) { impostors = n }

    fun playersCount(): Int = players.size
    fun selectedCategorySlug(): String = category.name


    fun reshuffleForNewMatch() { seed = System.currentTimeMillis() }

    fun hasWords(): Boolean = words.isNotEmpty()

    suspend fun ensureWordsLoaded(context: Context) {
        if (words.isEmpty()) {
            val loaded = withContext(Dispatchers.IO) {
                WordsRepository.loadWords(context, category)
            }
            words = loaded.shuffled(Random(seed))
        }
    }

    fun resetRoundState() {
        phase = Phase.ASSIGN
        currentIndex = 0
        roundWord = null
    }

    /** Carga palabras y baraja (una sola vez por partida). */
    fun startMatch(context: Context, customSeed: Long? = null, onReady: (() -> Unit)? = null) {
        seed = customSeed ?: System.currentTimeMillis()
        viewModelScope.launch {
            val loaded = withContext(Dispatchers.IO) {
                WordsRepository.loadWords(context, category)
            }
            words = loaded.shuffled(Random(seed))
            onReady?.invoke()
        }
    }

    /** Prepara la ronda: elige 1 palabra y los impostores, y resetea estado. */
    fun startRound() {
        roundWord = words.firstOrNull()                 // misma palabra para todos los no-impostores
        val r = Random(seed + 1)
        impostorSet = players.indices.shuffled(r).take(impostors).toSet()
        currentIndex = 0
        phase = Phase.ASSIGN
    }

    fun currentPlayerName(): String =
        players.getOrNull(currentIndex) ?: "Jugador ${currentIndex + 1}"

    fun isCurrentImpostor(): Boolean = currentIndex in impostorSet

    /** Maneja el tap. Devuelve true cuando TODOS ya vieron. */
    fun onTapNext(): Boolean {
        return when (phase) {
            Phase.ASSIGN -> { phase = Phase.REVEAL; false }
            Phase.REVEAL -> {
                if (currentIndex >= players.lastIndex) {
                    true // terminó la ronda de revelado
                } else {
                    currentIndex += 1
                    phase = Phase.ASSIGN
                    false
                }
            }
        }
    }
}
