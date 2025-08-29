package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class GetDatabaseTablesUseCase(
    private val databaseRepository: DatabaseRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceSelectedDatabaseUseCase: GetCurrentDeviceSelectedDatabaseUseCase,
) {
    suspend operator fun invoke(): Either<Throwable, List<String>> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("no current device"))
        val database = getCurrentDeviceSelectedDatabaseUseCase() ?: return Failure(Throwable("no selected database"))

        // Query SQLite system table to get all table names
        val query = "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' ORDER BY name"
        
        return databaseRepository.executeQuery(
            deviceIdAndPackageName = current,
            databaseId = database.id,
            query = query,
        ).mapSuccess { response ->
            when (response) {
                is DatabaseExecuteSqlResponseDomainModel.Select -> {
                    // Extract table names from the response
                    response.values.mapNotNull { row ->
                        row.getOrNull(0) // Get the first column (name) from each row
                    }
                }
                else -> emptyList() // If query fails or returns non-select result, return empty list
            }
        }
    }
}
