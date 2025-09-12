package com.example.impostorx.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.impostorx.data.WordsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

@SuppressLint("DefaultLocale")
@Composable
fun GameScreen(
    category: WordsRepository.Category,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current

    // Semilla de partida: mantiene el orden al rotar / volver
    val seed by rememberSaveable(category) { mutableStateOf(Random.nextLong()) }

    var words by remember { mutableStateOf<List<String>>(emptyList()) }
    var index by rememberSaveable(category, seed) { mutableStateOf(0) }

    // Carga y baraja con la semilla
    LaunchedEffect(category, seed) {
        val loaded = withContext(Dispatchers.IO) { WordsRepository.loadWords(ctx, category) }
        words = loaded.shuffled(Random(seed))
        index = 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Top bar
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                "←",
                color = Color.White,
                modifier = Modifier
                    .background(Color(0x22FFFFFF), RoundedCornerShape(8.dp))
                    .clickable { onBack() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = category.raw,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Palabra central (tap para siguiente)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF111111), RoundedCornerShape(12.dp))
                .clickable {
                    if (words.isNotEmpty()) {
                        index = (index + 1) % words.size
                    }
                }
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (words.isEmpty()) "Cargando…" else words[index],
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(12.dp))
        Text(
            text = "Toca para mostrar la siguiente palabra",
            color = Color.LightGray,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewGame() {
    GameScreen(
        category = WordsRepository.Category.Artistas,
        onBack = {}
    )
}
