package com.example.vaultofluck.core.time

/** Lightweight abstraction around the system clock for deterministic tests. */
interface GameClock {
    fun nowMillis(): Long
}

/** Real clock implementation. */
object RealClock : GameClock {
    override fun nowMillis(): Long = System.currentTimeMillis()
}
