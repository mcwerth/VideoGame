package com.example.vaultofluck.core.analytics

import android.util.Log

/**
 * Analytics facade to keep gameplay instrumentation consistent and testable.
 */
interface AnalyticsLogger {
    /** Logs a milestone or funnel checkpoint. */
    fun logEvent(name: String, attributes: Map<String, Any?> = emptyMap())
}

/**
 * Debug-friendly implementation that simply prints to logcat.
 */
class LogcatAnalyticsLogger : AnalyticsLogger {
    override fun logEvent(name: String, attributes: Map<String, Any?>) {
        Log.d("VaultAnalytics", "$name -> ${'$'}attributes")
    }
}
