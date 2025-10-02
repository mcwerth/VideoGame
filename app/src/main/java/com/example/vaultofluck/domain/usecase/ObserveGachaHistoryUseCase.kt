package com.example.vaultofluck.domain.usecase

import com.example.vaultofluck.data.repository.GameRepository
import com.example.vaultofluck.domain.model.GachaItem
import kotlinx.coroutines.flow.Flow

/** Stream exposing the last pulled items. */
class ObserveGachaHistoryUseCase(
    private val repository: GameRepository
) {
    operator fun invoke(): Flow<List<GachaItem>> = repository.gachaItems()
}
