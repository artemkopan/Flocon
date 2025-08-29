package io.github.openflocon.data.local.logs.datasource

import io.github.openflocon.domain.logs.models.LogCategory
import io.github.openflocon.domain.logs.models.LogEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class LogsLocalDataSourceInMemory : LogsLocalDataSource {
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())

    override fun observeLogEntries(): Flow<List<LogEntry>> = _logs

    override fun addLog(logEntry: LogEntry) {
        _logs.value = _logs.value + logEntry
    }

    override fun clearLogs() {
        _logs.value = emptyList()
    }

    override fun getLogsByCategory(category: LogCategory): Flow<List<LogEntry>> =
        _logs.map { logs -> logs.filter { it.category == category } }
}
