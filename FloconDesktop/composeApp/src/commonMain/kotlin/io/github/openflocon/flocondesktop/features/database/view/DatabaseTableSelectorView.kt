package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTableUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTablesState
import io.github.openflocon.flocondesktop.features.database.model.previewDatabaseTableUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun DatabaseTableSelectorView(
    tablesState: DatabaseTablesState,
    onTableSelected: (DatabaseTableUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    val contentPadding =
        PaddingValues(
            horizontal = 8.dp,
            vertical = 4.dp,
        )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Table : ",
            color = FloconTheme.colorPalette.onBackground,
            style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        )

        Spacer(modifier = Modifier.width(4.dp))

        when (tablesState) {
            DatabaseTablesState.Loading -> {
                Text(
                    modifier =
                    Modifier
                        .background(FloconTheme.colorPalette.onBackground.copy(alpha = 0.3f), shape = shape)
                        .padding(contentPadding),
                    text = "Loading...",
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colorPalette.onBackground,
                )
            }

            DatabaseTablesState.Empty -> {
                Text(
                    modifier =
                    Modifier
                        .background(FloconTheme.colorPalette.onBackground, shape = shape)
                        .padding(contentPadding),
                    text = "No Tables Found",
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colorPalette.background,
                )
            }

            is DatabaseTablesState.WithContent -> {
                var expanded by remember { mutableStateOf(false) }

                if (tablesState.selected != null) {
                    DatabaseTableView(
                        table = tablesState.selected,
                        textColor = FloconTheme.colorPalette.background,
                        modifier =
                        Modifier
                            .clip(shape)
                            .background(FloconTheme.colorPalette.onBackground)
                            .clickable { expanded = true }
                            .padding(contentPadding),
                    )
                } else {
                    Text(
                        modifier =
                        Modifier
                            .background(FloconTheme.colorPalette.onBackground.copy(alpha = 0.7f), shape = shape)
                            .clickable { expanded = true }
                            .padding(contentPadding),
                        text = "Select Table",
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colorPalette.background,
                    )
                }

                FloconDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    tablesState.tables.forEach { table ->
                        DropdownMenuItem(
                            text = {
                                DatabaseTableView(
                                    table = table,
                                    textColor = FloconTheme.colorPalette.onBackground,
                                    modifier = Modifier.padding(all = 4.dp),
                                )
                            },
                            onClick = {
                                onTableSelected(table)
                                expanded = false // Close the dropdown after selection
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DatabaseTableView(
    table: DatabaseTableUiModel,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = table.name,
        color = textColor,
        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
    )
}

@Preview
@Composable
private fun DatabaseTableViewPreview() {
    FloconTheme {
        DatabaseTableView(
            table = previewDatabaseTableUiModel(),
            textColor = FloconTheme.colorPalette.background,
        )
    }
}

@Preview
@Composable
private fun DatabaseTableSelectorViewPreview() {
    FloconTheme {
        DatabaseTableSelectorView(
            tablesState = DatabaseTablesState.WithContent(
                tables = listOf(
                    previewDatabaseTableUiModel().copy(name = "users"),
                    previewDatabaseTableUiModel().copy(name = "posts"),
                    previewDatabaseTableUiModel().copy(name = "comments"),
                ),
                selected = previewDatabaseTableUiModel().copy(name = "users"),
            ),
            onTableSelected = {},
        )
    }
}
