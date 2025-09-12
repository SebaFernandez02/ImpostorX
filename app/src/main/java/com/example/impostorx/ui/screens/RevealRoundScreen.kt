// RevealRoundScreen.kt
package com.example.impostorx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.impostorx.logic.GameViewModel
import androidx.compose.ui.tooling.preview.Preview



@Composable
fun RevealRoundScreen(
    onBack: () -> Unit,
    onAllRevealed: () -> Unit,
    gameVm: GameViewModel     // <- sin valor por defecto
) {
    val ctx = LocalContext.current
    LaunchedEffect(Unit) {
        gameVm.resetRoundState()
        gameVm.ensureWordsLoaded(ctx)
        gameVm.startRound()
    }

    RevealLayout(
        phase = gameVm.phase,
        playerName = gameVm.currentPlayerName(),
        isImpostor = gameVm.isCurrentImpostor(),
        word = gameVm.roundWord,
        onBack = onBack,
        onTap = {
            val done = gameVm.onTapNext()
            if (done) onAllRevealed()
        }
    )
}


/** Layout puro para usar en previews */
@Composable
private fun RevealLayout(
    phase: GameViewModel.Phase,
    playerName: String,
    isImpostor: Boolean,
    word: String?,
    onBack: () -> Unit,
    onTap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { onTap() }
            .padding(16.dp)
    ) {
        // Top bar
        Text(
            "←",
            color = Color.White,
            modifier = Modifier
                .background(Color(0x22FFFFFF), RoundedCornerShape(8.dp))
                .clickable { onBack() }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )

        Spacer(Modifier.height(24.dp))

        // Centro
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF111111), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (phase == GameViewModel.Phase.ASSIGN) {
                    Text("Pasar el celular a:", color = Color.White, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        playerName,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    val text = if (isImpostor) "Sos el Impostor" else (word ?: "Cargando…")
                    Text(
                        text,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            "Toca la pantalla para continuar",
            color = Color.LightGray,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}


@Preview(name = "Asignar jugador", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewReveal_Assign() {
    RevealLayout(
        phase = GameViewModel.Phase.ASSIGN,
        playerName = "Jugador 1",
        isImpostor = false,
        word = null,
        onBack = {},
        onTap = {}
    )
}

@Preview(name = "Revelar palabra (no impostor)", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewReveal_ShowWord() {
    RevealLayout(
        phase = GameViewModel.Phase.REVEAL,
        playerName = "Jugador 1",
        isImpostor = false,
        word = "Lionel Messi",
        onBack = {},
        onTap = {}
    )
}

@Preview(name = "Revelar impostor", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewReveal_Impostor() {
    RevealLayout(
        phase = GameViewModel.Phase.REVEAL,
        playerName = "Jugador 2",
        isImpostor = true,
        word = null,
        onBack = {},
        onTap = {}
    )
}
