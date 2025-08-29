package io.github.openflocon.domain.logs.repository

import io.github.openflocon.domain.logs.models.LogEntry
import kotlinx.coroutines.flow.Flow

interface LogsRepository {
    fun observeLogEntries(): Flow<List<LogEntry>>
    fun addLog(logEntry: LogEntry)
    fun clearLogs()
    fun getLogsByCategory(category: io.github.openflocon.domain.logs.models.LogCategory): Flow<List<LogEntry>>
}


