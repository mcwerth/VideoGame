package com.example.vaultofluck.testing

import com.example.vaultofluck.core.time.GameClock

class FakeClock(var now: Long = 0L) : GameClock {
    override fun nowMillis(): Long = now
    fun advance(millis: Long) { now += millis }
}
