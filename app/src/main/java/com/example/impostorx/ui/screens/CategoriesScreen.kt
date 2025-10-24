// CategoriesScreen.kt
package com.example.impostorx.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

data class CategoryUi(
    val slug: String,
    val title: String,
    val emoji: String,
    val hint: String = ""
)

private val defaultCategories = listOf(
    CategoryUi("Random",     "Random",     "🎲", "Cualquier categoría"),
    CategoryUi("Futbol",     "Fútbol",     "⚽", "Jugadores, clubes…"),
    CategoryUi("Deportes",   "Deportes",   "🏅", "Más allá del fútbol"),
    CategoryUi("Artistas",   "Artistas",   "🎨", "Música, cine, etc."),
    CategoryUi("Geografia",  "Geografía",  "🌍", "Países, ciudades…"),
    CategoryUi("Cine",       "Cine",       "🎬", "Películas y más"),
    CategoryUi("General",     "General",     "\uD83D\uDC40", "Cosas, profesiones, lugares y mas..."),
    CategoryUi("BrainRots",  "Brain rots", "\uD83C\uDDEE\uD83C\uDDF9", "Simplemente Brainrots")
)

@Composable
fun CategoriesScreen(
    totalPlayers: Int,
    onBack: () -> Unit,
    onCategorySelected: (slug: String) -> Unit,
    categories: List<CategoryUi> = defaultCategories
) {
    val haptics = LocalHapticFeedback.current
    var selected by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Top bar simple
        ScreenTopBar(
            title = "Categorías",
            onBack = onBack,
            trailing = {
                Text(
                    text = "$totalPlayers jugadores",
                    color = Color(0xFF9AA0A6),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        )


        Spacer(Modifier.height(18.dp))

        // Carrusel centrado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val itemSpacing = 14.dp
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(categories) { index, cat ->
                    val isSelected = index == selected
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.0f else 0.92f,
                        animationSpec = tween(220, easing = FastOutSlowInEasing),
                        label = "scale"
                    )

                    CategoryCard(
                        category = cat,
                        selected = isSelected,
                        modifier = Modifier
                            .widthIn(min = 220.dp, max = 280.dp)
                            .scale(scale)
                            .clickable {
                                selected = index
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Indicadores (puntos)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            categories.forEachIndexed { i, _ ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (i == selected) 10.dp else 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (i == selected) Color.White else Color(0x55FFFFFF))
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { onCategorySelected(categories[selected].slug) },
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            enabled = categories.isNotEmpty(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Elegir ${categories[selected].title}")
        }
    }
}

@Composable
private fun CategoryCard(
    category: CategoryUi,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) Color.White else Color(0x33FFFFFF)
    val bg = Brush.verticalGradient(
        colors = if (selected)
            listOf(Color(0xFF161616), Color(0xFF101010))
        else
            listOf(Color(0xFF121212), Color(0xFF0E0E0E))
    )

    Surface(
        modifier = modifier,
        color = Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = if (selected) 2.dp else 0.dp,
        shadowElevation = if (selected) 6.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .width(260.dp)
                .heightIn(min = 160.dp)
                .background(bg, RoundedCornerShape(20.dp))
                .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                category.emoji,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                category.title,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            if (category.hint.isNotBlank()) {
                Text(
                    category.hint,
                    color = Color(0xFFBDBDBD),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ScreenTopBar(
    title: String,
    onBack: () -> Unit,
    trailing: (@Composable () -> Unit)? = null
) {
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
            title,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) {
            Box(Modifier.padding(end = 4.dp)) { trailing() }
        }
    }
}

/* ---------- Preview ---------- */

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewCategories() {
    CategoriesScreen(
        totalPlayers = 8,
        onBack = {},
        onCategorySelected = {},
        categories = defaultCategories
    )
}
