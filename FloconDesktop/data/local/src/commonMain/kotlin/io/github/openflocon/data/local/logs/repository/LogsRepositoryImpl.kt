package io.github.openflocon.data.local.logs.repository

import io.github.openflocon.data.local.logs.datasource.LogsLocalDataSource
import io.github.openflocon.domain.logs.models.LogCategory
import io.github.openflocon.domain.logs.models.LogEntry
import io.github.openflocon.domain.logs.repository.LogsRepository
import kotlinx.coroutines.flow.Flow

class LogsRepositoryImpl(
    private val logsLocalDataSource: LogsLocalDataSource
) : LogsRepository {

    override fun observeLogEntries(): Flow<List<LogEntry>> =
        logsLocalDataSource.observeLogEntries()

    override fun addLog(logEntry: LogEntry) {
        logsLocalDataSource.addLog(logEntry)
    }

    override fun clearLogs() {
        logsLocalDataSource.clearLogs()
    }

    override fun getLogsByCategory(category: LogCategory): Flow<List<LogEntry>> =
        logsLocalDataSource.getLogsByCategory(category)
}


