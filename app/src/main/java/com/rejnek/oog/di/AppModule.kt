package com.rejnek.oog.di

import com.rejnek.oog.data.engine.JsEngineInterface
import com.rejnek.oog.data.engine.JsGameEngine
import com.rejnek.oog.data.repository.GameRepository
import com.rejnek.oog.ui.viewmodels.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Main module for application-wide dependencies
 */
val appModule = module {
    // Engine and Repository
    single {
        JsGameEngine(gameRepository = null, context = androidContext())
    } bind JsEngineInterface::class

    single {
        val jsEngine = get<JsEngineInterface>()
        val repository = GameRepository(jsEngine)

        // If the engine is a JsGameEngine, set the callback to the repository
        (jsEngine as? JsGameEngine)?.setCallback(repository)

        repository
    }

    // ViewModels
    viewModel { HomeViewModel(get()) }
    viewModel { GameStartViewModel(get()) }
    viewModel { GameTaskViewModel(get()) }
    viewModel { GameFinishViewModel(get()) }
}
