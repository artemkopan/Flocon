package io.github.openflocon.flocondesktop.features.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.logs.models.LogEntry
import io.github.openflocon.domain.logs.repository.LogsRepository
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProviderImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LogsViewModel(
    private val logsRepository: LogsRepository,
    private val dispatcherProvider: DispatcherProviderImpl
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    
    private val _uiState = MutableStateFlow(LogsUiState())
    val uiState: StateFlow<LogsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatcherProvider.data) {
            combine(
                logsRepository.observeLogEntries(),
                _searchQuery
            ) { logs, query ->
                val filteredLogs = logs.filter { 
                    if (query.isBlank()) true
                    else it.message.contains(query, ignoreCase = true) ||
                         it.deviceId?.contains(query, ignoreCase = true) == true
                }
                
                LogsUiState(
                    logs = filteredLogs,
                    searchQuery = query,
                    totalCount = logs.size,
                    filteredCount = filteredLogs.size
                )
            }.collect { 
                _uiState.value = it
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearLogs() {
        viewModelScope.launch(dispatcherProvider.data) {
            logsRepository.clearLogs()
        }
    }
}

data class LogsUiState(
    val logs: List<LogEntry> = emptyList(),
    val searchQuery: String = "",
    val totalCount: Int = 0,
    val filteredCount: Int = 0
)

