package com.example.vaultofluck.core.random

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RngTest {
    @Test
    fun weightedSelectionPrefersHigherWeights() {
        val rng = DefaultRng(seed = 42)
        val entries = listOf(
            WeightedEntry("Low", 1.0),
            WeightedEntry("High", 5.0)
        )
        val audit = rng.audit(entries, samples = 10_000)
        assertThat(audit.getValue("High")).isGreaterThan(audit.getValue("Low"))
    }

    @Test
    fun pityAdjustsWeights() {
        val base = mapOf("Common" to 0.6, "Epic" to 0.1)
        val pityApplied = applyPity(base, PityState(pullsSinceEpic = 30, pullsSinceLegendary = 30), softStart = 20, hardCap = 60, ramp = 0.05)
        assertThat(pityApplied.getValue("Epic")).isGreaterThan(base.getValue("Epic"))
    }
}
