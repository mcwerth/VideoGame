package com.example.vaultofluck.ui.navigation

/** Central navigation destinations used by the app. */
sealed class VaultDestination(val route: String) {
    data object Splash : VaultDestination("splash")
    data object Home : VaultDestination("home")
    data object Generators : VaultDestination("generators")
    data object Gacha : VaultDestination("gacha")
    data object Run : VaultDestination("run")
    data object Prestige : VaultDestination("prestige")
    data object Shop : VaultDestination("shop")
    data object Quests : VaultDestination("quests")
    data object Settings : VaultDestination("settings")
    data object Odds : VaultDestination("odds")
}
