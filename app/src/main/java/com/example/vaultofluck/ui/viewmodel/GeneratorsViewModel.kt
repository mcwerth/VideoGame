package com.example.vaultofluck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultofluck.core.result.GameResult
import com.example.vaultofluck.domain.model.Generator
import com.example.vaultofluck.domain.usecase.LoadDashboardUseCase
import com.example.vaultofluck.domain.usecase.UpgradeGeneratorUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** UI state for the generator management screen. */
data class GeneratorsUiState(
    val generators: List<Generator> = emptyList(),
    val message: String? = null
)

class GeneratorsViewModel(
    private val loadDashboard: LoadDashboardUseCase,
    private val upgradeGenerator: UpgradeGeneratorUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GeneratorsUiState())
    val state: StateFlow<GeneratorsUiState> = _state.asStateFlow()

    init {
        observeGenerators()
    }

    private fun observeGenerators() {
        viewModelScope.launch {
            loadDashboard().collectLatest { dashboard ->
                _state.value = _state.value.copy(generators = dashboard.generators)
            }
        }
    }

    fun upgrade(id: Int) {
        viewModelScope.launch {
            when (val result = upgradeGenerator(id)) {
                is GameResult.Success -> _state.value = _state.value.copy(message = "Upgraded ${'$'}{result.data.tier}")
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
