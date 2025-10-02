package com.example.vaultofluck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vaultofluck.AppContainer

/** Simple factory bridging the service locator with the ViewModel system. */
class VaultViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            HomeViewModel::class.java -> HomeViewModel(
                loadDashboard = container.loadDashboard,
                tickIdle = container.tickIdle,
                startRun = container.startRun
            ) as T
            GeneratorsViewModel::class.java -> GeneratorsViewModel(
                loadDashboard = container.loadDashboard,
                upgradeGenerator = container.upgradeGenerator
            ) as T
            GachaViewModel::class.java -> GachaViewModel(
                loadDashboard = container.loadDashboard,
                observeGachaHistory = container.observeGacha,
                pullGacha = container.pullGacha
            ) as T
            RunViewModel::class.java -> RunViewModel(
                loadDashboard = container.loadDashboard,
                startRun = container.startRun,
                endRun = container.endRun
            ) as T
            PrestigeViewModel::class.java -> PrestigeViewModel(
                loadDashboard = container.loadDashboard,
                doPrestige = container.doPrestige
            ) as T
            ShopViewModel::class.java -> ShopViewModel(
                loadDashboard = container.loadDashboard,
                applyUpgrade = container.applyUpgrade
            ) as T
            QuestsViewModel::class.java -> QuestsViewModel(
                observeQuests = container.observeQuests,
                claimQuest = container.claimQuest
            ) as T
            SettingsViewModel::class.java -> SettingsViewModel(
                preferences = container.preferences
            ) as T
            OddsViewModel::class.java -> OddsViewModel(
                loadDashboard = container.loadDashboard
            ) as T
            else -> throw IllegalArgumentException("Unknown ViewModel ${'$'}modelClass")
        }
    }
}
