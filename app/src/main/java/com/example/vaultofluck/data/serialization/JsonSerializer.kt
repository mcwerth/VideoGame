package com.example.vaultofluck.data.serialization

import kotlinx.serialization.json.Json

/** Global JSON configuration reused for Room payloads and preferences. */
object GameJson {
    val instance: Json = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
}
