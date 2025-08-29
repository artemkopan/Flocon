package io.github.openflocon.flocondesktop.features.logs

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val logsUiModule = module {
    viewModel { LogsViewModel(get(), get()) }
}

