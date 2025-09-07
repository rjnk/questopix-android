package com.rejnek.oog.di

import android.app.Application
import com.rejnek.oog.data.repository.GameRepository
import com.rejnek.oog.ui.viewmodels.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Main module for application-wide dependencies
 */
val appModule = module {
    single { GameRepository(get()) }

    // ViewModels
    viewModel { HomeViewModel(get()) }
    viewModel { LibraryViewModel(get()) }
    viewModel { GameInfoViewModel(get()) }
    viewModel { GameTaskViewModel(get()) }
    // Shared events ViewModel (activity scoped)
    viewModel { SharedEventsViewModel() }
}
