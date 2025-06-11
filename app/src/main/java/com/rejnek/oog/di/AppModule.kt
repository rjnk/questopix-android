package com.rejnek.oog.di

import android.content.Context
import com.rejnek.oog.data.engine.JsGameEngine
import com.rejnek.oog.data.repository.GameRepository
import com.rejnek.oog.ui.viewmodels.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Main module for application-wide dependencies
 */
val appModule = module {
    // Repositories
    single { JsGameEngine(androidContext()) }
    single { GameRepository(get()) }

    // ViewModels
    viewModel { HomeViewModel(get()) }
    viewModel { GameStartViewModel(get()) }
    viewModel { GameNavigationTextViewModel(get()) }
    viewModel { GameFinishViewModel(get()) }
}
