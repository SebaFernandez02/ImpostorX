package com.example.impostorx.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.impostorx.logic.GameViewModel
import kotlin.math.max
import kotlin.random.Random

@Composable
fun ImpostorCountScreen(
    totalPlayers: Int,
    category: String,
    onBack: () -> Unit,
    onConfirm: (impostors: Int, category: String) -> Unit,
    gameVm: GameViewModel
) {
    // Tu regla actual (mitad + 1). Si querés estricta mitad, cambiala a: max(1, totalPlayers / 2)
    val maxImpostors = max(1, (totalPlayers / 2))

    var selected by rememberSaveable { mutableStateOf(1) }
    var random by rememberSaveable { mutableStateOf(false) }

    fun choose(n: Int) {
        random = false
        selected = n.coerceIn(1, maxImpostors)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
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
                "Impostores",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            AssistChip(
                onClick = {},
                label = { Text(category, color = Color.White) },
                border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                colors = AssistChipDefaults.assistChipColors(containerColor = Color(0x11111111))
            )
        }

        // Centro bien centrado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0E0E0E))
                    .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .animateContentSize(spring()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "¿Cuántos impostores?",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Jugadores: $totalPlayers  ·  Máx: $maxImpostors",
                    color = Color(0xFFAAAAAA),
                    style = MaterialTheme.typography.bodySmall
                )

                // Chips (Random + números)
                val scroll = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scroll),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SelectChip(
                        text = "Random",
                        selected = random,
                        leading = {
                            Icon(
                                Icons.Rounded.Casino,
                                contentDescription = null,
                                tint = if (random) Color.Black else Color(0xFFB5B5B5)
                            )
                        },
                        onClick = { random = !random }
                    )
                    (1..maxImpostors).forEach { n ->
                        SelectChip(
                            text = n.toString(),
                            selected = !random && selected == n,
                            onClick = { choose(n) }
                        )
                    }
                }

                // Slider + estado
                Column(Modifier.fillMaxWidth(0.95f)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("1", color = Color(0xFF8A8A8A))
                        Text("$maxImpostors", color = Color(0xFF8A8A8A))
                    }
                    Slider(
                        value = selected.toFloat(),
                        onValueChange = { v -> choose(v.toInt()) },
                        valueRange = 1f..maxImpostors.toFloat(),
                        steps = (maxImpostors - 2).coerceAtLeast(0),
                        enabled = !random
                    )
                    Text(
                        text = if (random) "Seleccionado: Random (se sortea en cada partida)"
                        else "Seleccionado: $selected",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                AnimatedVisibility(visible = random) {
                    Text(
                        "Cada vez que toques “Volver a jugar” se recalcula la cantidad.",
                        color = Color(0xFF6ECF9A),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // CTA
        val finalForFirstMatch = remember(random, selected, maxImpostors) {
            if (random) (1..maxImpostors).random() else selected
        }
        Button(
            onClick = {
                // Guardar modo en VM (random por partida) y un valor inicial
                gameVm.setImpostorsRandomEnabled(random)
                gameVm.setImpostors(finalForFirstMatch)
                onConfirm(finalForFirstMatch, category)
            },
            modifier = Modifier
                .fillMaxWidth()
                .imePadding(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Listo",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/* ---------- UI helpers ---------- */

@Composable
private fun SelectChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    leading: (@Composable () -> Unit)? = null
) {
    val bg = if (selected) Color.White else Color(0x15151515)
    val fg = if (selected) Color.Black else Color(0xFFB5B5B5)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, if (selected) Color.White else Color(0x33FFFFFF), RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .animateContentSize(spring())
    ) {
        if (leading != null) { leading(); Spacer(Modifier.width(6.dp)) }
        Text(text, color = fg, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}

/* ---------- Preview ---------- */

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewImpostorCount() {
    ImpostorCountScreen(
        totalPlayers = 8,
        category = "Cine",
        onBack = {},
        onConfirm = { _, _ -> },
        gameVm = GameViewModel()
    )
}
