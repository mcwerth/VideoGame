package com.example.vaultofluck.testing

import com.example.vaultofluck.core.random.Rng
import com.example.vaultofluck.core.random.WeightedEntry

class FakeRng(private val sequence: MutableList<Double> = mutableListOf()) : Rng {
    fun enqueue(value: Double) { sequence.add(value) }
    override fun nextDouble(): Double = if (sequence.isEmpty()) 0.5 else sequence.removeAt(0)
    override fun <T> pickWeighted(entries: List<WeightedEntry<T>>): T {
        val normalized = entries.sortedBy { it.weight }
        val roll = nextDouble() * entries.sumOf { it.weight }
        var acc = 0.0
        for (entry in normalized) {
            acc += entry.weight
            if (roll <= acc) return entry.value
        }
        return normalized.last().value
    }
    override fun <T> audit(entries: List<WeightedEntry<T>>, samples: Int): Map<T, Double> = emptyMap()
}
