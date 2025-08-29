package io.github.openflocon.data.local.logs.datasource

import io.github.openflocon.domain.logs.models.LogCategory
import io.github.openflocon.domain.logs.models.LogEntry
import kotlinx.coroutines.flow.Flow

interface LogsLocalDataSource {
    fun observeLogEntries(): Flow<List<LogEntry>>
    fun addLog(logEntry: LogEntry)
    fun clearLogs()
    fun getLogsByCategory(category: LogCategory): Flow<List<LogEntry>>
}
