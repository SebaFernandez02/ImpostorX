// GameViewModel.kt
package com.example.impostorx.logic

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.impostorx.data.WordsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.logging.Logger
import kotlin.math.max
import kotlin.random.Random

class GameViewModel : ViewModel() {

    enum class Phase { ASSIGN, REVEAL }

    // Config
    private var players: List<String> = emptyList()
    private var category = WordsRepository.Category.Random

    private var impostorRandom: Boolean = false
    private var impostors: Int = 1

    // Palabras
    private var seed: Long = System.currentTimeMillis()
    private var words: List<String> = emptyList()

    // 👇 ESTADO OBSERVABLE POR COMPOSE
    var roundWord by mutableStateOf<String?>(null)
        private set
    var phase by mutableStateOf(Phase.ASSIGN)
        private set
    var currentIndex by mutableStateOf(0)
        private set

    var matchToken by mutableStateOf(0)
        private set

    private var impostorSet: Set<Int> = emptySet()
    fun resetImpostorSet() { impostorSet = emptySet()
    }

    // setters de config
    fun setPlayers(list: List<String>) { players = list }
    fun selectCategory(cat: WordsRepository.Category) { category = cat }
    //fun setImpostorsFixed(random : Boolean, count: Int) { impostorRandom = random; impostors = count }
    fun setImpostorsRandomEnabled(enabled: Boolean) { impostorRandom = enabled }
    fun isImpostorRandomEnabled(): Boolean = impostorRandom
    fun getImpostors(): Int = impostors
    fun setImpostors(count: Int) {impostors = count}

    private fun maxImpostors(): Int =
        max(1, if (players.size > 4) (players.size / 2) + 1 else players.size / 2)

    fun playersCount(): Int = players.size
    fun selectedCategorySlug(): String = category.name

    fun resetRoundState() {
        phase = Phase.ASSIGN
        currentIndex = 0
        roundWord = null
    }

    fun reshuffleForNewMatch() {
        seed = System.currentTimeMillis()
        words = emptyList()
    }

    suspend fun ensureWordsLoaded(context: Context) {
        if (words.isEmpty()) {
            val loaded = withContext(Dispatchers.IO) {
                WordsRepository.loadWords(context, category)
            }
            words = loaded.shuffled(Random(seed))
        }
    }

    fun startMatch(context: Context, onReady: (() -> Unit)? = null) {

        matchToken += 1

        impostorSet = emptySet()

        viewModelScope.launch {
            ensureWordsLoaded(context)
            onReady?.invoke()
        }
    }

    /** Se llama al empezar CADA partida/ronda */
    fun startRound() {
        impostorSet = emptySet()

        if (impostorRandom) {
            impostors = (1..playersCount()).random()
            Logger.getLogger("Impostorx").info("Random impostors: $impostors")
        }
        roundWord = words.firstOrNull()
        val r = Random(seed + System.currentTimeMillis())
        impostorSet = players.indices.shuffled(r).take(impostors).toSet()

        // reset de estado visible
        currentIndex = 0
        phase = Phase.ASSIGN
    }

    fun currentPlayerName(): String =
        players.getOrNull(currentIndex) ?: "Jugador ${currentIndex + 1}"

    fun isCurrentImpostor(): Boolean = currentIndex in impostorSet

    /** Tap en pantalla */
    fun onTapNext(): Boolean = when (phase) {
        Phase.ASSIGN -> { phase = Phase.REVEAL; false }
        Phase.REVEAL -> {
            if (currentIndex >= players.lastIndex) true
            else { phase = Phase.ASSIGN; currentIndex += 1; false }
        }
    }
}
