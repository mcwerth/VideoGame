package com.example.vaultofluck.domain.usecase

import com.example.vaultofluck.data.repository.GameRepository
import com.example.vaultofluck.domain.model.DashboardState
import kotlinx.coroutines.flow.Flow

/** Exposes the main dashboard flow consumed across screens. */
class LoadDashboardUseCase(
    private val repository: GameRepository
) {
    operator fun invoke(): Flow<DashboardState> = repository.dashboard()
}
