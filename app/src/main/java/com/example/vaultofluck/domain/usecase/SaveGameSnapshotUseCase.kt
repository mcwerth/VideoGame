package com.example.vaultofluck.domain.usecase

import com.example.vaultofluck.data.repository.GameRepository

/** Persists key timestamps when the app is backgrounded. */
class SaveGameSnapshotUseCase(
    private val repository: GameRepository
) {
    suspend operator fun invoke(now: Long = System.currentTimeMillis()) {
        repository.updatePlayerLastSeen(now)
    }
}
