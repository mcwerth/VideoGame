package com.example.vaultofluck.domain.usecase

import com.example.vaultofluck.core.result.GameResult
import com.example.vaultofluck.data.repository.GameRepository
import com.example.vaultofluck.domain.util.RunUpdate

/** Starts a new roguelite run if none is active. */
class StartRunUseCase(
    private val repository: GameRepository
) {
    suspend operator fun invoke(depth: Int): GameResult<RunUpdate> {
        val now = System.currentTimeMillis()
        val update = repository.startRun(now, depth)
        return GameResult.Success(update)
    }
}
