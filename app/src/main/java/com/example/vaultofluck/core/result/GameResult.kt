package com.example.vaultofluck.core.result

/** Simple sealed result used by domain use-cases. */
sealed class GameResult<out T> {
    data class Success<T>(val data: T) : GameResult<T>()
    data class Error(val reason: String) : GameResult<Nothing>()
}
