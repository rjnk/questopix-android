package com.rejnek.oog.di

import com.rejnek.oog.data.repository.GameRepository
import com.rejnek.oog.ui.viewmodels.*
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Main module for application-wide dependencies
 */
val appModule = module {
    // Repositories
    single { GameRepository() }
    
    // ViewModels
    viewModel { HomeViewModel(get()) }
    viewModel { GameStartViewModel(get()) }
    viewModel { GameTaskViewModel(get()) }
    viewModel { GameNavigationTextViewModel(get()) }
    viewModel { GameFinishViewModel(get()) }
}
