package com.example.impostorx.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.impostorx.logic.GameViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.impostorx.data.WordsRepository

object Routes {
    const val Home = "home"
    const val Room = "room"
    const val Categories = "categories/{total}"              // recibe total de jugadores
    const val Impostors = "impostors/{total}/{category}"     // recibe total + categoría
    const val Reveal = "reveal"
    const val RoundReady = "roundReady"   //

}

// ImpostorApp.kt
@Composable
fun ImpostorApp() {
    val nav = rememberNavController()
    val gameVm: GameViewModel = viewModel()  // <-- único VM compartido

    NavHost(navController = nav, startDestination = Routes.Home) {

        composable(Routes.Home) {
            ImpostorHome { nav.navigate(Routes.Room) }
        }

        // Pantalla de nombres: PASAR LISTA COMPLETA
        composable(Routes.Room) {
            ImpostorRoomScreen(
                onBack = { nav.navigateUp() },
                onContinue = { players: List<String> ->
                    gameVm.setPlayers(players)
                    nav.navigate("categories/${players.size}")
                },
                gameVm = gameVm
            )
        }

        composable(Routes.Categories) { backStack ->
            val total = backStack.arguments?.getInt("total") ?: 1
            CategoriesScreen(
                onBack = { nav.navigateUp() },
                onCategorySelected = { catSlug ->
                    gameVm.selectCategory(WordsRepository.Category.valueOf(catSlug))
                    nav.navigate("impostors/$total/$catSlug")
                },
                gameVm = gameVm
            )
        }

        composable(Routes.Impostors) { backStack ->
            val cat = backStack.arguments?.getString("category") ?: "Random"
            val ctx = LocalContext.current

            ImpostorCountScreen(
                totalPlayers = gameVm.playersCount(),
                category = cat,
                onBack = { nav.navigateUp() },
                onConfirm = { _, _ ->
                    gameVm.startMatch(ctx) {
                        nav.navigate(Routes.Reveal) {
                            launchSingleTop = true
                        }
                    }
                },
                gameVm = gameVm
            )
        }


        composable(Routes.Reveal) {
            RevealRoundScreen(
                onBack = {
                    // volver a elegir impostores con mismos jugadores/categoría
                    val total = gameVm.playersCount()
                    val cat = gameVm.selectedCategorySlug()
                    gameVm.resetRoundState()
                    gameVm.resetImpostorSet()
                    nav.navigate("impostors/$total/$cat") {
                        popUpTo(Routes.Reveal) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onAllRevealed = {
                    nav.navigate(Routes.RoundReady) {
                        popUpTo(Routes.Reveal) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                gameVm = gameVm   // <-- MISMO VM
            )
        }

        composable(Routes.RoundReady) {
            val ctx = LocalContext.current
            RoundReadyScreen(
                onBackToImpostors = {
                    val total = gameVm.playersCount()
                    val cat = gameVm.selectedCategorySlug()
                    gameVm.resetRoundState()
                    nav.navigate("impostors/$total/$cat") {
                        popUpTo(Routes.RoundReady) { inclusive = true }
                        launchSingleTop = true
                    }
                    gameVm.reshuffleForNewMatch()
                },
                onPlayAgain = {
                    // misma config (jugadores/categoría/impostores), pero partida nueva
                    gameVm.resetRoundState()
                    gameVm.reshuffleForNewMatch()
                    gameVm.startMatch(ctx) {
                        nav.navigate(Routes.Reveal) {
                            popUpTo(Routes.RoundReady) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

    }
}
