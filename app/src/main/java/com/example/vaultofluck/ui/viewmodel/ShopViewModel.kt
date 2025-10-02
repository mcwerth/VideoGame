package com.example.vaultofluck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultofluck.core.result.GameResult
import com.example.vaultofluck.domain.model.Upgrade
import com.example.vaultofluck.domain.usecase.ApplyUpgradeUseCase
import com.example.vaultofluck.domain.usecase.LoadDashboardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** Displays purchasable upgrades and meta shop items. */
data class ShopUiState(
    val upgrades: List<Upgrade> = emptyList(),
    val message: String? = null
)

class ShopViewModel(
    private val loadDashboard: LoadDashboardUseCase,
    private val applyUpgrade: ApplyUpgradeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ShopUiState())
    val state: StateFlow<ShopUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            loadDashboard().collectLatest { dashboard ->
                _state.value = _state.value.copy(upgrades = dashboard.upgrades)
            }
        }
    }

    fun buyUpgrade(id: Int) {
        viewModelScope.launch {
            when (val result = applyUpgrade(id)) {
                is GameResult.Success -> _state.value = _state.value.copy(message = "Upgrade improved")
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
