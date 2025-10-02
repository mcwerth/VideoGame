package com.example.vaultofluck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultofluck.core.result.GameResult
import com.example.vaultofluck.domain.model.Quest
import com.example.vaultofluck.domain.usecase.ClaimQuestUseCase
import com.example.vaultofluck.domain.usecase.ObserveQuestsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** Manages quest progress and claims. */
data class QuestsUiState(
    val quests: List<Quest> = emptyList(),
    val message: String? = null
)

class QuestsViewModel(
    private val observeQuests: ObserveQuestsUseCase,
    private val claimQuest: ClaimQuestUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(QuestsUiState())
    val state: StateFlow<QuestsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeQuests().collectLatest { quests ->
                _state.value = _state.value.copy(quests = quests)
            }
        }
    }

    fun claim(id: Int) {
        viewModelScope.launch {
            when (val result = claimQuest(id)) {
                is GameResult.Success -> _state.value = _state.value.copy(message = "Reward claimed")
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
