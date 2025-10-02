package com.example.vaultofluck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultofluck.core.result.GameResult
import com.example.vaultofluck.domain.model.CurrencyIds
import com.example.vaultofluck.domain.model.GachaItem
import com.example.vaultofluck.domain.usecase.LoadDashboardUseCase
import com.example.vaultofluck.domain.usecase.ObserveGachaHistoryUseCase
import com.example.vaultofluck.domain.usecase.PullGachaUseCase
import com.example.vaultofluck.domain.util.GachaSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** UI state for gacha pulls and odds disclosure. */
data class GachaUiState(
    val tokens: Double = 0.0,
    val pity: Int = 0,
    val lastItems: List<GachaItem> = emptyList(),
    val audit: Map<String, Double> = emptyMap(),
    val message: String? = null,
    val isRolling: Boolean = false
)

class GachaViewModel(
    private val loadDashboard: LoadDashboardUseCase,
    private val observeGachaHistory: ObserveGachaHistoryUseCase,
    private val pullGacha: PullGachaUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GachaUiState())
    val state: StateFlow<GachaUiState> = _state.asStateFlow()

    init {
        observeInventory()
        observeHistory()
    }

    private fun observeInventory() {
        viewModelScope.launch {
            loadDashboard().collectLatest { dashboard ->
                val tokens = dashboard.currencies.firstOrNull { it.name == CurrencyIds.TOKENS }?.amount ?: 0.0
                _state.value = _state.value.copy(tokens = tokens, pity = dashboard.settings.gachaPity)
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            observeGachaHistory().collectLatest { items ->
                _state.value = _state.value.copy(lastItems = items)
            }
        }
    }

    fun pull(pullSize: Int) {
        if (_state.value.isRolling) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isRolling = true)
            when (val result = pullGacha(pullSize)) {
                is GameResult.Success -> applySummary(result.data)
                is GameResult.Error -> _state.value = _state.value.copy(message = result.reason, isRolling = false)
            }
        }
    }

    private fun applySummary(summary: GachaSummary) {
        _state.value = _state.value.copy(
            lastItems = summary.pull.items,
            pity = summary.pull.pityAfter,
            audit = summary.audit,
            isRolling = false
        )
    }

    fun consumeMessage() {
        if (_state.value.message != null) {
            _state.value = _state.value.copy(message = null)
        }
    }
}
