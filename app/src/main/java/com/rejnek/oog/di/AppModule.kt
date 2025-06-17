package com.rejnek.oog.di

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
    // Engine and Repository
    single {
        // Create JsGameEngine first without any callback
        JsGameEngine(callback = null, context = androidContext())
    }

    single {
        // Create GameRepository with JsGameEngine dependency
        val repository = GameRepository(get<JsGameEngine>())

        // Get the JsGameEngine instance and update its callback to use the repository
        val engine = get<JsGameEngine>()

        // Set the repository as the callback for the engine using a custom set method
        // This is done through reflection or a setter method in JsGameEngine
        setEngineCallback(engine, repository)

        repository
    }

    // ViewModels
    viewModel { HomeViewModel(get()) }
    viewModel { GameStartViewModel(get()) }
    viewModel { GameTaskViewModel(get()) }
    viewModel { GameFinishViewModel(get()) }
}

/**
 * Helper function to set callback on JsGameEngine instance via reflection
 * This breaks the circular dependency in Koin
 */
private fun setEngineCallback(engine: JsGameEngine, callback: GameRepository) {
    val field = JsGameEngine::class.java.getDeclaredField("callback")
    field.isAccessible = true
    field.set(engine, callback)
}
