package com.example.vaultofluck.domain.usecase

import com.example.vaultofluck.core.result.GameResult
import com.example.vaultofluck.data.repository.GameRepository
import com.example.vaultofluck.domain.model.Quest

/** Claims quest rewards when objectives are met. */
class ClaimQuestUseCase(
    private val repository: GameRepository
) {
    suspend operator fun invoke(id: Int): GameResult<Quest> {
        val quest = repository.claimQuest(id) ?: return GameResult.Error("Quest missing")
        return GameResult.Success(quest)
    }
}
