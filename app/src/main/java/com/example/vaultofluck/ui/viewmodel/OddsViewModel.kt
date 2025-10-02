package com.example.vaultofluck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultofluck.core.economy.Economy
import com.example.vaultofluck.domain.usecase.LoadDashboardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** Presents odds, pity thresholds, and expected values. */
data class OddsUiState(
    val rarityWeights: Map<String, Double> = emptyMap(),
    val pitySoftStart: Int = Economy.pitySoftStart,
    val pityHardCap: Int = Economy.pityHardCap,
    val pityCurrent: Int = 0,
    val evSingles: Double = 0.0,
    val evTens: Double = 0.0,
    val evFifties: Double = 0.0
)

class OddsViewModel(
    private val loadDashboard: LoadDashboardUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(
        OddsUiState(
            rarityWeights = Economy.rarityWeights,
            evSingles = expectedValue(1),
            evTens = expectedValue(10),
            evFifties = expectedValue(50)
        )
    )
    val state: StateFlow<OddsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            loadDashboard().collectLatest { dashboard ->
                _state.value = _state.value.copy(pityCurrent = dashboard.settings.gachaPity)
            }
        }
    }

    private fun expectedValue(pullSize: Int): Double {
        val base = Economy.rarityWeights
        val multiplier = when (pullSize) {
            1 -> 1.0
            10 -> 1.02
            50 -> 1.05
            else -> 1.0
        }
        return base.entries.sumOf { (_, weight) -> weight } * multiplier
    }
}
