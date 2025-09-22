package com.example.impostorx.ui.screens

// RoundReadyScreen.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.impostorx.ui.components.ScreenTopBar

@Composable
fun RoundReadyScreen(
    onBackToImpostors: () -> Unit,
    onPlayAgain: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {


        Spacer(Modifier.height(24.dp))

        // Centro
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "¡Que empiece la ronda!",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Botón volver a jugar
        Button(
            onClick = onPlayAgain,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Volver a jugar")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewRoundReady() {
    RoundReadyScreen(onBackToImpostors = {}, onPlayAgain = {})
}
