package com.example.vaultofluck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultofluck.core.result.GameResult
import com.example.vaultofluck.domain.model.Currency
import com.example.vaultofluck.domain.model.DashboardState
import com.example.vaultofluck.domain.model.Run
import com.example.vaultofluck.domain.usecase.LoadDashboardUseCase
import com.example.vaultofluck.domain.usecase.StartRunUseCase
import com.example.vaultofluck.domain.usecase.TickIdleUseCase
import com.example.vaultofluck.domain.util.IdleTick
import com.example.vaultofluck.domain.util.RunUpdate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** UI model for the home dashboard. */
data class HomeUiState(
    val currencies: List<Currency> = emptyList(),
    val currentRun: Run? = null,
    val lastIdleTick: IdleTick? = null,
    val message: String? = null
)

class HomeViewModel(
    private val loadDashboard: LoadDashboardUseCase,
    private val tickIdle: TickIdleUseCase,
    private val startRun: StartRunUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        observeDashboard()
    }

    private fun observeDashboard() {
        viewModelScope.launch {
            loadDashboard().collectLatest { dashboard: DashboardState ->
                _state.value = _state.value.copy(
                    currencies = dashboard.currencies,
                    currentRun = dashboard.currentRun
                )
            }
        }
    }

    fun onResume(now: Long) {
        viewModelScope.launch {
            val tick = tickIdle(now)
            _state.value = _state.value.copy(lastIdleTick = tick)
        }
    }

    fun startRun(depth: Int) {
        viewModelScope.launch {
            when (val result = startRun(depth)) {

                is GameResult.Success<RunUpdate> -> handleRun(result.data)

                is GameResult.Error -> _state.value = _state.value.copy(message = result.reason)
            }
        }
    }

    private fun handleRun(update: RunUpdate) {
        _state.value = _state.value.copy(currentRun = update.run, message = if (update.wasNew) null else "Run already active")
    }

    fun consumeMessage() {
        if (_state.value.message != null) {
            _state.value = _state.value.copy(message = null)
        }
    }
}
