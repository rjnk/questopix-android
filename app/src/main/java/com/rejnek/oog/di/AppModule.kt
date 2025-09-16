package com.rejnek.oog.di

import com.rejnek.oog.data.repository.GameRepository
import com.rejnek.oog.ui.viewmodel.*
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Main module for application-wide dependencies
 */
val appModule = module {
    single { GameRepository(get()) }

    // ViewModels
    viewModel { LoadBundledViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { LibraryViewModel(get()) }
    viewModel { GameInfoViewModel(get()) }
    viewModel { GameTaskViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    // Shared events ViewModel (activity scoped)
    viewModel { SharedEventsViewModel() }
}
