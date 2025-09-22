// ImpostorRoomScreen.kt
package com.example.impostorx.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.example.impostorx.logic.GameViewModel
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ImpostorRoomScreen(
    onBack: () -> Unit,
    onContinue: (players: List<String>) -> Unit,
    gameVm: GameViewModel
) {
    val focus = LocalFocusManager.current

    // Estado local (se lo pasás al VM al continuar)
    val names = remember { mutableStateListOf<String>() }
    var input by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var showConfirmClear by rememberSaveable { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    fun normalizeName(raw: String) =
        raw.trim().replace(Regex("\\s+"), " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

    fun tryAdd() {
        val n = normalizeName(input)
        error = when {
            n.isBlank() -> "Escribí un nombre"
            n.length > 24 -> "Máximo 24 caracteres"
            names.any { it.equals(n, ignoreCase = true) } -> "Ese nombre ya está"
            else -> null
        }
        if (error == null) {
            names += n
            input = ""
            // 🔁 seguir tipeando sin tocar el campo
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }

    fun remove(name: String) { names.remove(name) }
    fun clearAll() { names.clear() }

    val canContinue = names.size >= 3 // regla mínima sugerida
    val headerGradient = Brush.linearGradient(listOf(Color(0xFF111111), Color(0xFF0A0A0A)))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(headerGradient)
                .padding(8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Jugadores",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            // Borrar todo (solo si hay nombres)
            AnimatedVisibility(visible = names.isNotEmpty()) {
                TextButton(onClick = { showConfirmClear = true }) {
                    Icon(Icons.Rounded.DeleteSweep, contentDescription = null, tint = Color(0xFFFF6B6B))
                    Spacer(Modifier.width(4.dp))
                    Text("Borrar todo", color = Color(0xFFFF6B6B))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Input + botón agregar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF111111))
                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                .padding(6.dp)
        ) {
            TextField(
                value = input,
                onValueChange = {
                    // (Opcional) si el usuario escribe coma o Enter, agregá automáticamente
                    if (it.endsWith('\n') || it.endsWith(',')) {
                        input = it.trimEnd('\n', ',')
                        tryAdd()
                    } else {
                        input = it
                        if (error != null) error = null
                    }
                },
                placeholder = { Text("Escribe un nombre…", color = Color(0x66FFFFFF)) },
                singleLine = true,
                isError = error != null,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp)
                    .focusRequester(focusRequester),         // 👈 importante
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    errorTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Words
                ),
                keyboardActions = KeyboardActions(onDone = { tryAdd() })
            )

            Spacer(Modifier.width(6.dp))

            FilledIconButton(
                onClick = { tryAdd() },         // 👈 vuelve a enfocar adentro de tryAdd
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF2ECC71))
            ) { Icon(Icons.Rounded.Add, contentDescription = "Agregar", tint = Color.Black)}
        }
        AnimatedVisibility(visible = error != null) {
            Text(error.orEmpty(), color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 6.dp))
        }

        Spacer(Modifier.height(16.dp))

        // Chips de nombres (wrap)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = Color(0xFF0E0E0E),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (names.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Agrega al menos 3 jugadores", color = Color(0x66FFFFFF))
                }
            } else {
                Column(Modifier.fillMaxSize().padding(12.dp)) {
                    // contador + barajar opcional
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "${names.size} jugador${if (names.size == 1) "" else "es"}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            "Barajar",
                            color = Color(0xFF66D1FF),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    // shuffle in-place
                                    val shuffled = names.shuffled(Random(System.currentTimeMillis()))
                                    names.clear(); names.addAll(shuffled)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        names.forEach { name ->
                            PlayerChip(name = name, onRemove = { remove(name) })
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Botón continuar
        Button(
            onClick = {
                gameVm.setPlayers(names.toList())
                onContinue(names.toList())
            },
            enabled = canContinue,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                if (canContinue) "Listo" else "Agrega mínimo 3",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    // Confirmación borrar todo
    if (showConfirmClear) {
        AlertDialog(
            onDismissRequest = { showConfirmClear = false },
            title = { Text("Borrar todos los jugadores") },
            text = { Text("Esta acción eliminará la lista completa.") },
            confirmButton = {
                TextButton(onClick = { showConfirmClear = false; clearAll() }) {
                    Text("Borrar", color = Color(0xFFFF6B6B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClear = false }) {
                    Text("Cancelar")
                }
            },
            containerColor = Color(0xFF161616)
        )
    }
}

@Composable
private fun PlayerChip(name: String, onRemove: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(name, color = Color.White, modifier = Modifier.padding(end = 6.dp))
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = "Eliminar",
            tint = Color(0xFFBBBBBB),
            modifier = Modifier
                .size(16.dp)
                .clickable { onRemove() }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewImpostorRoom() {
    val vm = GameViewModel()
    ImpostorRoomScreen(onBack = {}, onContinue = {}, gameVm = vm)
}
