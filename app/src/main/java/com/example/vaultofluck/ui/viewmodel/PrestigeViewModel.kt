package com.example.vaultofluck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultofluck.core.economy.Economy
import com.example.vaultofluck.core.result.GameResult
import com.example.vaultofluck.domain.model.CurrencyIds
import com.example.vaultofluck.domain.usecase.DoPrestigeUseCase
import com.example.vaultofluck.domain.usecase.LoadDashboardUseCase
import com.example.vaultofluck.domain.util.PrestigeResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** Handles prestige preview and execution. */
data class PrestigeUiState(
    val coins: Double = 0.0,
    val essencePreview: Long = 0,
    val lastResult: PrestigeResult? = null,
    val message: String? = null
)

class PrestigeViewModel(
    private val loadDashboard: LoadDashboardUseCase,
    private val doPrestige: DoPrestigeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PrestigeUiState())
    val state: StateFlow<PrestigeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            loadDashboard().collectLatest { dashboard ->
                val coins = dashboard.currencies.firstOrNull { it.name == CurrencyIds.COINS }?.amount ?: 0.0
                val preview = Economy.prestigeReturn(coins)
                _state.value = _state.value.copy(coins = coins, essencePreview = preview)
            }
        }
    }

    fun prestige() {
        viewModelScope.launch {
            when (val result = doPrestige()) {
                is GameResult.Success -> _state.value = _state.value.copy(lastResult = result.data, message = null)
                is GameResult.Error -> _state.value = _state.value.copy(message = result.reason)
            }
        }
    }

    fun consumeMessage() {
        if (_state.value.message != null) {
            _state.value = _state.value.copy(message = null)
        }
    }
}
