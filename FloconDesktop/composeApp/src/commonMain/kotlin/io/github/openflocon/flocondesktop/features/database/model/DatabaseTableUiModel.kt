package io.github.openflocon.flocondesktop.features.database.model

import androidx.compose.runtime.Immutable

@Immutable
data class DatabaseTableUiModel(
    val name: String,
    val databaseId: String,
)

@Immutable
data class DatabaseTableColumn(
    val name: String,
    val type: String,
    val nullable: Boolean,
    val primaryKey: Boolean,
    val defaultValue: String?,
)

@Immutable
data class DatabaseTableSchema(
    val tableName: String,
    val columns: List<DatabaseTableColumn>,
)

sealed interface DatabaseTablesState {
    data object Loading : DatabaseTablesState
    data object Empty : DatabaseTablesState
    data class WithContent(
        val tables: List<DatabaseTableUiModel>,
        val selected: DatabaseTableUiModel?,
    ) : DatabaseTablesState
}

fun previewDatabaseTableUiModel() = DatabaseTableUiModel(
    name = "users",
    databaseId = "database_1",
)

fun previewDatabaseTableColumn() = DatabaseTableColumn(
    name = "id",
    type = "INTEGER",
    nullable = false,
    primaryKey = true,
    defaultValue = null,
)

fun previewDatabaseTableSchema() = DatabaseTableSchema(
    tableName = "users",
    columns = listOf(
        DatabaseTableColumn("id", "INTEGER", false, true, null),
        DatabaseTableColumn("name", "TEXT", false, false, null),
        DatabaseTableColumn("email", "TEXT", true, false, null),
        DatabaseTableColumn("created_at", "INTEGER", false, false, "CURRENT_TIMESTAMP"),
    ),
)
