package com.example.vaultofluck.core.economy

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EconomyTest {
    @Test
    fun generatorCostIncreasesExponentially() {
        val cost1 = Economy.generatorLevelCost(1, 1)
        val cost5 = Economy.generatorLevelCost(1, 5)
        assertThat(cost5).isGreaterThan(cost1)
    }

    @Test
    fun prestigeReturnsDiminishing() {
        val low = Economy.prestigeReturn(1_000.0)
        val high = Economy.prestigeReturn(1_000_000.0)
        assertThat(high).isGreaterThan(low)
        val ratio = high.toDouble() / 1_000_000.0
        val ratioLow = low.toDouble() / 1_000.0
        assertThat(ratio).isLessThan(ratioLow)
    }
}
