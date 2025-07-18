package com.rejnek.oog.di

import android.app.Application
import com.rejnek.oog.data.repository.GameRepository
import com.rejnek.oog.data.gameItems.direct.map.MapViewModel
import com.rejnek.oog.ui.viewmodels.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Main module for application-wide dependencies
 */
val appModule = module {
    single { GameRepository(get()) }

    // ViewModels
    viewModel { HomeViewModel(get()) }
    viewModel { GameTaskViewModel(get()) }
    viewModel { GameFinishViewModel(get()) }
    viewModel { GameMenuViewModel(get()) }
    viewModel { MapViewModel(
        androidContext().applicationContext as Application,
        get()
    ) }
}
