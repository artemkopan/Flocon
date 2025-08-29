package io.github.openflocon.data.local.logs

import io.github.openflocon.data.local.logs.datasource.LogsLocalDataSource
import io.github.openflocon.data.local.logs.datasource.LogsLocalDataSourceInMemory
import io.github.openflocon.data.local.logs.repository.LogsRepositoryImpl
import io.github.openflocon.domain.logs.Logger
import io.github.openflocon.domain.logs.repository.LogsRepository
import org.koin.dsl.module

val logsLocalModule = module {
    single<LogsLocalDataSource> { LogsLocalDataSourceInMemory() }
    single<LogsRepository> { LogsRepositoryImpl(get()) }
    single<Logger> { Logger(get()) }
}
