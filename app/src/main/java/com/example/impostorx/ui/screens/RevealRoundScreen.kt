// RevealRoundScreen.kt
package com.example.impostorx.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
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
    gameVm: GameViewModel
) {
    KeepScreenOn()
    val ctx = LocalContext.current
    val haptics = LocalHapticFeedback.current

    // ⚠️ se vuelve a ejecutar cada partida (clave: matchToken)
    LaunchedEffect(gameVm.matchToken) {
        gameVm.resetRoundState()
        gameVm.ensureWordsLoaded(ctx)
        gameVm.startRound()
    }

    // 🔔 Vibra al entrar en REVEAL (distinto si es impostor)
    LaunchedEffect(gameVm.phase, gameVm.currentIndex) {
        if (gameVm.phase == GameViewModel.Phase.REVEAL) {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }


    val totalPlayers = gameVm.playersCount()
    val revealedCount = (gameVm.currentIndex + if (gameVm.phase == GameViewModel.Phase.REVEAL) 1 else 0)
        .coerceIn(0, totalPlayers)

    RevealLayout(
        phase = gameVm.phase,
        playerName = gameVm.currentPlayerName(),
        isImpostor = gameVm.isCurrentImpostor(),
        word = gameVm.roundWord,
        totalPlayers = totalPlayers,
        revealedCount = revealedCount,
        onBack = onBack,
        onTap = {
            val done = gameVm.onTapNext()
            if (done) onAllRevealed()
        }
    )

}

@Composable
private fun RevealLayout(
    phase: GameViewModel.Phase,
    playerName: String,
    isImpostor: Boolean,
    word: String?,
    totalPlayers: Int,
    revealedCount: Int,
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
        // Top bar moderno
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF101010))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text(
                "ImpostorX",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(6.dp))


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

        Spacer(Modifier.height(16.dp))

        // Píldoras de progreso (una por jugador)
        if (totalPlayers > 0) {
            PillsProgress(
                total = totalPlayers,
                revealed = revealedCount
            )
            Spacer(Modifier.height(8.dp))
        }

        Text(
            "Toca la pantalla para continuar",
            color = Color.LightGray,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
@Composable
private fun PillsProgress(total: Int, revealed: Int) {
    // Estados:
    //   < revealed  → revelado
    //   == revealed → próximo/actual (destacado)
    //   > revealed  → pendiente
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { i ->
            val stateColor = when {
                i < revealed -> Color(0xFF2ECC71) // revelado
                i == revealed -> Color.White      // actual
                else -> Color(0xFF444444)        // pendiente
            }
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .weight(1f, fill = false)
                    .widthIn(min = 10.dp) // para que no queden demasiado finitas
                    .background(stateColor, RoundedCornerShape(999.dp))
            )
        }
    }

}

@Composable
private fun KeepScreenOn() {
    val view = LocalView.current
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }
}

/* ---------- Previews ---------- */

@Preview(name = "Asignar jugador", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewReveal_Assign() {
    RevealLayout(
        phase = GameViewModel.Phase.ASSIGN,
        playerName = "Jugador 1",
        isImpostor = false,
        word = null,
        totalPlayers = 6,
        revealedCount = 0,
        onBack = {},
        onTap = {}
    )
}

@Preview(name = "Revelar palabra", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewReveal_ShowWord() {
    RevealLayout(
        phase = GameViewModel.Phase.REVEAL,
        playerName = "Jugador 1",
        isImpostor = false,
        word = "Lionel Messi",
        totalPlayers = 6,
        revealedCount = 1,
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
        totalPlayers = 6,
        revealedCount = 2,
        onBack = {},
        onTap = {}
    )
}