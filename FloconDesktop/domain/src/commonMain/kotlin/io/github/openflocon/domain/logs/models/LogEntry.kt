package io.github.openflocon.domain.logs.models

import kotlin.time.Clock
import kotlin.time.Instant


data class LogEntry(
    val id: String,
    val timestamp: Instant = Clock.System.now(),
    val level: LogLevel,
    val category: LogCategory,
    val deviceId: String? = null,
    val message: String,
    val details: String? = null,
    val exception: String? = null,
)

enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}

enum class LogCategory {
    WEBSOCKET_CONNECTION,
    WEBSOCKET_MESSAGE,
    WEBSOCKET_ERROR,
    DEVICE_CONNECTION,
    SERVER,
    NETWORK,
    GENERAL
}


