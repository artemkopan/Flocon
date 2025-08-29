package io.github.openflocon.flocondesktop.features.logs.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.domain.logs.models.LogEntry
import io.github.openflocon.domain.logs.models.LogLevel
import io.github.openflocon.flocondesktop.features.logs.LogsViewModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LogsScreen(modifier: Modifier = Modifier) {
    val viewModel: LogsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LogsScreenContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onClearLogs = viewModel::clearLogs,
        modifier = modifier
    )
}

@Composable
fun LogsScreenContent(
    uiState: io.github.openflocon.flocondesktop.features.logs.LogsUiState,
    onSearchQueryChange: (String) -> Unit,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(lazyListState)

    FloconSurface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            FloconPageTopBar(
                modifier = Modifier.fillMaxWidth(),
                filterBar = {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Search logs...") },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                },
                actions = {
                    Text(
                        text = "${uiState.filteredCount}/${uiState.totalCount} logs",
                        style = MaterialTheme.typography.bodySmall
                    )
                    FloconButton(onClick = onClearLogs) {
                        Text("Clear Logs")
                    }
                }
            )
            


            // Logs list
            Box(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.logs,
                            key = { it.id }
                        ) { logEntry ->
                            LogEntryItem(
                                logEntry = logEntry,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    FloconVerticalScrollbar(
                        adapter = scrollAdapter,
                        modifier = Modifier.fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
fun LogEntryItem(
    logEntry: LogEntry,
    modifier: Modifier = Modifier
) {
    val warningColor = Color(0xFFF39C12) // Orange color for warnings
    
    val backgroundColor = when (logEntry.level) {
        LogLevel.ERROR -> FloconTheme.colorPalette.error.copy(alpha = 0.1f)
        LogLevel.WARN -> warningColor.copy(alpha = 0.1f)
        LogLevel.INFO -> FloconTheme.colorPalette.primary.copy(alpha = 0.05f)
        LogLevel.DEBUG -> FloconTheme.colorPalette.surface
    }
    
    val textColor = when (logEntry.level) {
        LogLevel.ERROR -> FloconTheme.colorPalette.error
        LogLevel.WARN -> warningColor
        LogLevel.INFO -> FloconTheme.colorPalette.onSurface
        LogLevel.DEBUG -> FloconTheme.colorPalette.onSurface.copy(alpha = 0.7f)
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = logEntry.level.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    )
                    Text(
                        text = logEntry.category.name.replace('_', ' '),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = FloconTheme.colorPalette.primary
                        )
                    )
                    logEntry.deviceId?.let { deviceId ->
                        Text(
                            text = "Device: ${deviceId.take(8)}...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
                Text(
                    text = logEntry.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                        .let { "${it.time.hour}:${String.format("%02d", it.time.minute)}:${String.format("%02d", it.time.second)}" },
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.6f)
                    )
                )
            }

            // Message
            Text(
                text = logEntry.message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = textColor,
                    lineHeight = 20.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            // Details
            logEntry.details?.let { details ->
                Text(
                    text = details,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Exception
            logEntry.exception?.let { exception ->
                Text(
                    text = exception,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = FloconTheme.colorPalette.error.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

