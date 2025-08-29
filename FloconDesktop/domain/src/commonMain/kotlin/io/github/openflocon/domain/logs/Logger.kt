package io.github.openflocon.domain.logs

import io.github.openflocon.domain.logs.models.LogCategory
import io.github.openflocon.domain.logs.models.LogEntry
import io.github.openflocon.domain.logs.models.LogLevel
import io.github.openflocon.domain.logs.repository.LogsRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class Logger(
    private val logsRepository: LogsRepository
) {
    fun debug(
        category: LogCategory,
        message: String,
        deviceId: String? = null,
        details: String? = null
    ) {
        log(LogLevel.DEBUG, category, message, deviceId, details)
    }

    fun info(
        category: LogCategory,
        message: String,
        deviceId: String? = null,
        details: String? = null
    ) {
        log(LogLevel.INFO, category, message, deviceId, details)
    }

    fun warn(
        category: LogCategory,
        message: String,
        deviceId: String? = null,
        details: String? = null,
        exception: Throwable? = null
    ) {
        log(LogLevel.WARN, category, message, deviceId, details, exception)
    }

    fun error(
        category: LogCategory,
        message: String,
        deviceId: String? = null,
        details: String? = null,
        exception: Throwable? = null
    ) {
        log(LogLevel.ERROR, category, message, deviceId, details, exception)
    }

    private fun log(
        level: LogLevel,
        category: LogCategory,
        message: String,
        deviceId: String? = null,
        details: String? = null,
        exception: Throwable? = null
    ) {
        val logEntry = LogEntry(
            id = Uuid.random().toString(),
            level = level,
            category = category,
            deviceId = deviceId,
            message = message,
            details = details,
            exception = exception?.stackTraceToString()
        )
        logsRepository.addLog(logEntry)
        
        // Log entry is stored in repository for UI display
        // Console logging is handled by the data layer implementation if needed
    }
}


