package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTableColumn
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTableSchema
import io.github.openflocon.flocondesktop.features.database.model.previewDatabaseTableSchema
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DatabaseSchemaView(
    schema: DatabaseTableSchema,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = FloconTheme.colorPalette.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Table header
            Text(
                text = "Table: ${schema.tableName}",
                style = FloconTheme.typography.titleLarge,
                color = FloconTheme.colorPalette.onSurface,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "${schema.columns.size} column${if (schema.columns.size != 1) "s" else ""}",
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.7f),
            )

            HorizontalDivider(
                color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.2f),
            )

            // Column headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Column",
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colorPalette.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(2f),
                )
                Text(
                    text = "Type",
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colorPalette.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.5f),
                )
                Text(
                    text = "Nullable",
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colorPalette.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "Default",
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colorPalette.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.5f),
                )
            }

            HorizontalDivider(
                color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.2f),
            )

            // Columns list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(schema.columns) { column ->
                    DatabaseSchemaColumnView(
                        column = column,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun DatabaseSchemaColumnView(
    column: DatabaseTableColumn,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = if (column.primaryKey) {
                    FloconTheme.colorPalette.primary.copy(alpha = 0.1f)
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(4.dp),
            )
            .padding(vertical = 4.dp, horizontal = if (column.primaryKey) 8.dp else 0.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Column name with primary key indicator
            Row(
                modifier = Modifier.weight(2f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (column.primaryKey) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "Primary Key",
                        tint = FloconTheme.colorPalette.primary,
                    )
                }
                Text(
                    text = column.name,
                    style = FloconTheme.typography.bodyMedium,
                    color = FloconTheme.colorPalette.onSurface,
                    fontWeight = if (column.primaryKey) FontWeight.SemiBold else FontWeight.Normal,
                )
            }

            // Type
            Text(
                text = column.type,
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.weight(1.5f),
            )

            // Nullable
            Text(
                text = if (column.nullable) "Yes" else "No",
                style = FloconTheme.typography.bodyMedium,
                color = if (column.nullable) {
                    FloconTheme.colorPalette.onSurface.copy(alpha = 0.6f)
                } else {
                    FloconTheme.colorPalette.error.copy(alpha = 0.8f)
                },
                modifier = Modifier.weight(1f),
            )

            // Default value
            Text(
                text = column.defaultValue ?: "-",
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.weight(1.5f),
            )
        }
    }
}

@Preview
@Composable
private fun DatabaseSchemaViewPreview() {
    FloconTheme {
        DatabaseSchemaView(
            schema = previewDatabaseTableSchema(),
        )
    }
}

@Preview
@Composable
private fun DatabaseSchemaColumnViewPreview() {
    FloconTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            DatabaseSchemaColumnView(
                column = DatabaseTableColumn(
                    name = "id",
                    type = "INTEGER",
                    nullable = false,
                    primaryKey = true,
                    defaultValue = null,
                ),
            )
            DatabaseSchemaColumnView(
                column = DatabaseTableColumn(
                    name = "name",
                    type = "TEXT",
                    nullable = false,
                    primaryKey = false,
                    defaultValue = null,
                ),
            )
            DatabaseSchemaColumnView(
                column = DatabaseTableColumn(
                    name = "email",
                    type = "TEXT",
                    nullable = true,
                    primaryKey = false,
                    defaultValue = "NULL",
                ),
            )
        }
    }
}
