package com.example.impostorx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import com.example.impostorx.logic.GameViewModel


@Composable
fun ImpostorRoomScreen(
    onBack: () -> Unit,
    onContinue: (totalPlayers: List<String>) -> Unit,
    gameVm: GameViewModel
){
    var input by rememberSaveable { mutableStateOf("") }
    val names = remember { mutableStateListOf<String>() }
    var error by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    fun send() {
        val t = input.trim()
        if (t.isEmpty()) return
        val existsIdx = names.indexOfFirst { it.equals(t, ignoreCase = true) }
        if (existsIdx >= 0) {
            error = "Ese nombre ya existe"
            scope.launch { listState.animateScrollToItem(existsIdx) }
        } else {
            names.add(t)
            input = ""
            error = null
            scope.launch { listState.animateScrollToItem(names.lastIndex) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Top bar (← vuelve)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "←",
                color = Color.White,
                modifier = Modifier
                    .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp))
                    .clickable { onBack() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Impostor",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(12.dp))

        // Caja grande (lista)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .background(Color(0xFF111111), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = names,
                    key = { index, name -> "$name-$index" }
                ) { index, name ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = name,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "✕",
                            color = Color.LightGray,
                            modifier = Modifier
                                .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp))
                                .clickable { names.removeAt(index) } // borrar individual
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Input + botón enviar (→)
        val canSend = input.trim().isNotEmpty() &&
                names.none { it.equals(input.trim(), ignoreCase = true) }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input,
                onValueChange = {
                    input = it
                    error = null
                },
                modifier = Modifier
                    .weight(1f)
                    .onKeyEvent { ev ->
                        if (ev.type == KeyEventType.KeyUp && ev.key == Key.Enter) {
                            send(); true
                        } else false
                    },
                singleLine = true,
                placeholder = { Text("Escribe…", color = Color.Gray) },

                // 👉 texto blanco
                textStyle = TextStyle(color = Color.White),

                // (opcional) colores del TextField para dark theme
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    disabledTextColor = Color.LightGray,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.LightGray,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),

                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onSend = { send() },
                    onDone = { send() }
                )
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "→",
                color = if (canSend) Color.White else Color.Gray,
                modifier = Modifier
                    .border(
                        1.dp,
                        if (canSend) Color.LightGray else Color.DarkGray,
                        RoundedCornerShape(6.dp)
                    )
                    .clickable(enabled = canSend) { send() } // botón enviar
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }

        // Mensaje de error (duplicado)
        if (error != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                error!!,
                color = Color(0xFFFF6B6B),
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(12.dp))

        // Botones inferiores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                names.clear()
                error = null
            }) { Text("Borrar") }

            Button(onClick = { onContinue(names.toList()) }) { Text("Listo") }

        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun PreviewImpostorRoom() {
    ImpostorRoomScreen(onBack = {}, onContinue = {}, gameVm = GameViewModel())
}
