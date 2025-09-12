package com.example.impostorx.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.impostorx.logic.GameViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoriesScreen(
    onBack: () -> Unit,
    onCategorySelected: (String) -> Unit,
    gameVm: GameViewModel
) {
    val categories = listOf("Random", "Futbol", "Deportes", "Artistas", "Geografia", "Cine")
    val pagerState = rememberPagerState(pageCount = { categories.size })
    val scope = rememberCoroutineScope()

    fun goPrev() {
        val target = if (pagerState.currentPage == 0) categories.lastIndex else pagerState.currentPage - 1
        scope.launch { pagerState.animateScrollToPage(target) }
    }
    fun goNext() {
        val target = if (pagerState.currentPage == categories.lastIndex) 0 else pagerState.currentPage + 1
        scope.launch { pagerState.animateScrollToPage(target) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Top bar
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(4.dp)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) { Text("←", color = Color.White) }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Categorias", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Carrusel: flecha | pager | flecha
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(top = 180.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Izquierda
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .fillMaxHeight()
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .clickable { goPrev() },
                contentAlignment = Alignment.Center
            ) { Text("←", color = Color.White) }

            Spacer(Modifier.width(12.dp))

            // Pager central (tarjeta clickeable)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                pageSpacing = 12.dp,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
                        .clickable { onCategorySelected(categories[page]) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categories[page],
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Derecha
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .fillMaxHeight()
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .clickable { goNext() },
                contentAlignment = Alignment.Center
            ) { Text("→", color = Color.White) }
        }

        Spacer(Modifier.height(12.dp))

        // Indicadores (puntitos)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(categories.size) { i ->
                val selected = i == pagerState.currentPage
                Box(
                    Modifier
                        .size(if (selected) 8.dp else 6.dp)
                        .padding(3.dp)
                        .background(if (selected) Color.White else Color.Gray, RoundedCornerShape(50))
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("Selecciona una categoría", color = Color.LightGray, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 130.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewCategories() {
    CategoriesScreen(onBack = {}, onCategorySelected = {}, gameVm = GameViewModel())
}
