package com.example.vaultofluck.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.vaultofluck.ui.navigation.VaultDestination
import com.example.vaultofluck.ui.screens.gacha.GachaRoute
import com.example.vaultofluck.ui.screens.generators.GeneratorsRoute
import com.example.vaultofluck.ui.screens.home.HomeRoute
import com.example.vaultofluck.ui.screens.home.SplashScreen
import com.example.vaultofluck.ui.screens.odds.OddsRoute
import com.example.vaultofluck.ui.screens.prestige.PrestigeRoute
import com.example.vaultofluck.ui.screens.quests.QuestsRoute
import com.example.vaultofluck.ui.screens.run.RunRoute
import com.example.vaultofluck.ui.screens.settings.SettingsRoute
import com.example.vaultofluck.ui.screens.shop.ShopRoute
import com.example.vaultofluck.ui.viewmodel.GachaViewModel
import com.example.vaultofluck.ui.viewmodel.GeneratorsViewModel
import com.example.vaultofluck.ui.viewmodel.HomeViewModel
import com.example.vaultofluck.ui.viewmodel.OddsViewModel
import com.example.vaultofluck.ui.viewmodel.PrestigeViewModel
import com.example.vaultofluck.ui.viewmodel.QuestsViewModel
import com.example.vaultofluck.ui.viewmodel.RunViewModel
import com.example.vaultofluck.ui.viewmodel.SettingsViewModel
import com.example.vaultofluck.ui.viewmodel.ShopViewModel
import com.example.vaultofluck.ui.viewmodel.VaultViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultOfLuckRoot(factory: VaultViewModelFactory) {
    val navController = rememberNavController()
    val backstack by navController.currentBackStackEntryAsState()
    val currentRoute = backstack?.destination?.route ?: VaultDestination.Splash.route
    val bottomDestinations = listOf(
        VaultDestination.Home,
        VaultDestination.Generators,
        VaultDestination.Gacha,
        VaultDestination.Run,
        VaultDestination.Shop
    )
    Scaffold(
        bottomBar = {
            if (currentRoute != VaultDestination.Splash.route) {
                NavigationBar {
                    bottomDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            label = { Text(destination.route.replaceFirstChar { it.uppercase() }) },
                            icon = { Text(destination.route.first().uppercase()) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = VaultDestination.Splash.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(VaultDestination.Splash.route) {
                SplashScreen(navController)
            }
            composable(VaultDestination.Home.route) {
                val viewModel: HomeViewModel = viewModel(factory = factory)
                HomeRoute(
                    viewModel = viewModel,
                    onNavigateGacha = { navController.navigate(VaultDestination.Gacha.route) },
                    onNavigateRun = { navController.navigate(VaultDestination.Run.route) },
                    onNavigatePrestige = { navController.navigate(VaultDestination.Prestige.route) },
                    onNavigateQuests = { navController.navigate(VaultDestination.Quests.route) },
                    onNavigateSettings = { navController.navigate(VaultDestination.Settings.route) },
                    onNavigateOdds = { navController.navigate(VaultDestination.Odds.route) }
                )
            }
            composable(VaultDestination.Generators.route) {
                val viewModel: GeneratorsViewModel = viewModel(factory = factory)
                GeneratorsRoute(viewModel)
            }
            composable(VaultDestination.Gacha.route) {
                val viewModel: GachaViewModel = viewModel(factory = factory)
                GachaRoute(viewModel)
            }
            composable(VaultDestination.Run.route) {
                val viewModel: RunViewModel = viewModel(factory = factory)
                RunRoute(viewModel)
            }
            composable(VaultDestination.Prestige.route) {
                val viewModel: PrestigeViewModel = viewModel(factory = factory)
                PrestigeRoute(viewModel)
            }
            composable(VaultDestination.Shop.route) {
                val viewModel: ShopViewModel = viewModel(factory = factory)
                ShopRoute(viewModel)
            }
            composable(VaultDestination.Quests.route) {
                val viewModel: QuestsViewModel = viewModel(factory = factory)
                QuestsRoute(viewModel)
            }
            composable(VaultDestination.Settings.route) {
                val viewModel: SettingsViewModel = viewModel(factory = factory)
                SettingsRoute(viewModel)
            }
            composable(VaultDestination.Odds.route) {
                val viewModel: OddsViewModel = viewModel(factory = factory)
                OddsRoute(viewModel)
            }
        }
    }
}
