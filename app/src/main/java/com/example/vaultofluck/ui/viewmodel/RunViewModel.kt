package com.example.vaultofluck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultofluck.core.result.GameResult
import com.example.vaultofluck.domain.model.Run
import com.example.vaultofluck.domain.usecase.EndRunUseCase
import com.example.vaultofluck.domain.usecase.LoadDashboardUseCase
import com.example.vaultofluck.domain.usecase.StartRunUseCase
import com.example.vaultofluck.domain.util.RunUpdate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** Tracks run progress and completion results. */
data class RunUiState(
    val activeRun: Run? = null,
    val message: String? = null
)

class RunViewModel(
    private val loadDashboard: LoadDashboardUseCase,
    private val startRun: StartRunUseCase,
    private val endRun: EndRunUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RunUiState())
    val state: StateFlow<RunUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            loadDashboard().collectLatest { dashboard ->
                _state.value = _state.value.copy(activeRun = dashboard.currentRun)
            }
        }
    }

    fun start(depth: Int) {
        viewModelScope.launch {
            when (val result = startRun(depth)) {
                is GameResult.Success -> applyRun(result.data)
                is GameResult.Error -> _state.value = _state.value.copy(message = result.reason)
            }
        }
    }

    fun finish(success: Boolean, depth: Int) {
        viewModelScope.launch {
            when (val result = endRun(success, depth)) {
                is GameResult.Success -> applyRun(result.data)
                is GameResult.Error -> _state.value = _state.value.copy(message = result.reason)
            }
        }
    }

    private fun applyRun(update: RunUpdate) {
        _state.value = _state.value.copy(activeRun = update.run)
    }

    fun consumeMessage() {
        if (_state.value.message != null) {
            _state.value = _state.value.copy(message = null)
        }
    }
}
