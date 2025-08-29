package io.github.openflocon.flocondesktop.features.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.usecase.ExecuteDatabaseQueryUseCase
import io.github.openflocon.domain.database.usecase.GetDatabaseTablesUseCase
import io.github.openflocon.domain.database.usecase.ObserveLastSuccessQueriesUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.database.delegate.DatabaseSelectorDelegate
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenState
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTableUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTablesState
import io.github.openflocon.flocondesktop.features.database.model.DeviceDataBaseUiModel
import io.github.openflocon.flocondesktop.features.database.model.QueryResultUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DatabaseViewModel(
    private val databaseSelectorDelegate: DatabaseSelectorDelegate,
    private val executeDatabaseQueryUseCase: ExecuteDatabaseQueryUseCase,
    private val getDatabaseTablesUseCase: GetDatabaseTablesUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val observeLastSuccessQueriesUseCase: ObserveLastSuccessQueriesUseCase,
) : ViewModel(databaseSelectorDelegate) {
    val deviceDataBases: StateFlow<DatabasesStateUiModel> = databaseSelectorDelegate.deviceDataBases

    private val queryResult = MutableStateFlow<QueryResultUiModel?>(null)
    private val databaseTables = MutableStateFlow<DatabaseTablesState>(DatabaseTablesState.Empty)
    
    val tablesState: StateFlow<DatabaseTablesState> = databaseTables
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DatabaseTablesState.Empty,
        )

    val recentQueries: StateFlow<List<String>> = observeLastSuccessQueriesUseCase()
        .map { it.take(5) }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    val state: StateFlow<DatabaseScreenState> = queryResult.map { queryResult ->
        if (queryResult == null) DatabaseScreenState.Idle
        else DatabaseScreenState.Result(queryResult)
    }.flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DatabaseScreenState.Idle,
        )

    fun executeQuery(query: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            executeDatabaseQueryUseCase(
                query = query,
            ).fold(doOnSuccess = {
                queryResult.value = it.toUi()
            }, doOnFailure = {
                feedbackDisplayer.displayMessage("database failure : $it")
            })
        }
    }

    fun clearQuery() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            queryResult.update { null }
        }
    }

    private fun DatabaseExecuteSqlResponseDomainModel.toUi(): QueryResultUiModel = when (this) {
        is DatabaseExecuteSqlResponseDomainModel.Error -> QueryResultUiModel.Text(this.message)
        is DatabaseExecuteSqlResponseDomainModel.Insert -> QueryResultUiModel.Text("Inserted (insertedId=$insertedId)")
        DatabaseExecuteSqlResponseDomainModel.RawSuccess -> QueryResultUiModel.Text("Success")
        is DatabaseExecuteSqlResponseDomainModel.Select ->
            QueryResultUiModel.Values(
                columns = this.columns,
                rows =
                values.map {
                    DatabaseRowUiModel(it)
                },
            )

        is DatabaseExecuteSqlResponseDomainModel.UpdateDelete -> QueryResultUiModel.Text("Done, affected=$affectedCount")
    }

    fun onDatabaseSelected(database: DeviceDataBaseUiModel) {
        databaseSelectorDelegate.onDatabaseSelected(database)
        // When a database is selected, load its tables
        loadDatabaseTables()
    }

    fun onTableSelected(table: DatabaseTableUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            // Update selected table
            val currentState = databaseTables.value
            if (currentState is DatabaseTablesState.WithContent) {
                databaseTables.update { 
                    currentState.copy(selected = table)
                }
            }
            
            // Automatically execute SELECT * query for the selected table
            executeQuery("SELECT * FROM ${table.name}")
        }
    }

    private fun loadDatabaseTables() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            databaseTables.update { DatabaseTablesState.Loading }
            
            getDatabaseTablesUseCase().fold(
                doOnSuccess = { tableNames ->
                    val tables = tableNames.map { tableName ->
                        DatabaseTableUiModel(
                            name = tableName,
                            databaseId = databaseSelectorDelegate.deviceDataBases.value.let { state ->
                                if (state is DatabasesStateUiModel.WithContent) {
                                    state.selected.id
                                } else {
                                    ""
                                }
                            }
                        )
                    }
                    databaseTables.update { 
                        if (tables.isNotEmpty()) {
                            DatabaseTablesState.WithContent(
                                tables = tables,
                                selected = null
                            )
                        } else {
                            DatabaseTablesState.Empty
                        }
                    }
                },
                doOnFailure = { error ->
                    databaseTables.update { DatabaseTablesState.Empty }
                    feedbackDisplayer.displayMessage("Failed to load tables: $error")
                }
            )
        }
    }

    fun onVisible() {
        databaseSelectorDelegate.start()
    }

    fun onNotVisible() {
        databaseSelectorDelegate.stop()
    }
}
