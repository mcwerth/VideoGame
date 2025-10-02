package com.example.vaultofluck.domain.usecase

import com.example.vaultofluck.data.repository.GameRepository
import com.example.vaultofluck.domain.model.Quest
import kotlinx.coroutines.flow.Flow

/** Streams quests for UI consumption. */
class ObserveQuestsUseCase(
    private val repository: GameRepository
) {
    operator fun invoke(): Flow<List<Quest>> = repository.quests()
}
