package com.example.impostorx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.impostorx.logic.GameViewModel
import kotlin.math.max

@Composable
fun ImpostorCountScreen(
    totalPlayers: Int,
    category: String,
    onBack: () -> Unit,
    onConfirm:  (impostors: Int, category: String) -> Unit,
    gameVm: GameViewModel
) {
    val maxImpostors = max(1,   (totalPlayers / 2) + 1 )

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
        // TOP BAR (queda arriba)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) { Text("←", color = Color.White) }

            Box(Modifier.fillMaxWidth().padding(end = 36.dp), contentAlignment = Alignment.Center) {
                Text(
                    "Impostores",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // BLOQUE CENTRAL CENTRADO VERTICALMENTE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),                 // ocupa el alto disponible
            contentAlignment = Alignment.Center // centra en Y (y X)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.widthIn(max = 420.dp)   // limita ancho para estética
            ) {
                Text(
                    text = "Ingresa el número de impostores",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )

                // Chips centrados + scroll
                val scroll = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scroll),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Chip(
                        text = "Random",
                        selected = random,
                        onClick = { random = !random }
                    )
                    (1..maxImpostors).forEach { n ->
                        Chip(
                            text = n.toString(),
                            selected = !random && selected == n,
                            onClick = { choose(n) }
                        )
                    }
                }

                // Slider + label
                Column(Modifier.fillMaxWidth(0.9f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1", color = Color.LightGray)
                        Text("$maxImpostors", color = Color.LightGray)
                    }
                    Slider(
                        value = selected.toFloat(),
                        onValueChange = { v -> choose(v.toInt()) },
                        valueRange = 1f..maxImpostors.toFloat(),
                        steps = (maxImpostors - 2).coerceAtLeast(0),
                        enabled = !random
                    )
                    Text(
                        text = if (random) "Seleccionado: Random" else "Seleccionado: $selected",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }


        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                val final = if (random) (1..totalPlayers).random() else selected
                // <-- guardar en el VM el modo elegido
                gameVm.setImpostorsRandomEnabled(random)
                gameVm.setImpostors(final)

                onConfirm(final, category) // navega como ya tenías
            },
            modifier = Modifier.fillMaxWidth().imePadding()
        ) { Text("Listo") }

    }
}


@Composable
private fun Chip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(
                1.dp,
                if (selected) Color.White else Color.LightGray,
                RoundedCornerShape(50)
            )
            .background(
                if (selected) Color(0x22FFFFFF) else Color.Transparent,
                RoundedCornerShape(50)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.LightGray,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

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
