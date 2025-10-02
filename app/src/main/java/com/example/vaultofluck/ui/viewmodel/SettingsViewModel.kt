package com.example.vaultofluck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultofluck.data.prefs.GamePreferences
import com.example.vaultofluck.data.prefs.GamePreferencesDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** Handles mutable settings, accessibility toggles, and debug hooks. */
data class SettingsUiState(
    val preferences: GamePreferences = GamePreferences(true, 0.8f, false, null)
)

class SettingsViewModel(
    private val preferences: GamePreferencesDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.preferences.collectLatest { prefs ->
                _state.value = SettingsUiState(prefs)
            }
        }
    }

    fun toggleAnimations(enabled: Boolean) {
        viewModelScope.launch { preferences.updateAnimations(enabled) }
    }

    fun toggleLargeText(enabled: Boolean) {
        viewModelScope.launch { preferences.updateLargeText(enabled) }
    }

    fun setVolume(volume: Float) {
        viewModelScope.launch { preferences.updateSfxVolume(volume) }
    }

    fun setDebugSeed(seed: Int?) {
        viewModelScope.launch { preferences.updateDebugSeed(seed) }
    }
}
