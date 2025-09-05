package com.rejnek.oog.data.gameItems.direct.commands

import com.rejnek.oog.data.gameItems.GenericDirectFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class Save : GenericDirectFactory() {
    override val id = "save"

    override val js: String
        get() =
            """
            function captureAllVariables() {
                const state = {};
                
                // Capture all variables starting with underscore from global scope
                const globalScope = (typeof window !== 'undefined') ? window : globalThis;
                Object.getOwnPropertyNames(globalScope).forEach(varName => {
                    if (varName.startsWith('_')) {
                        state[varName] = globalScope[varName];
                    }
                });
                
                return state;
            }

            function save() {
                const gameState = captureAllVariables();
                directAction('save', JSON.stringify(gameState));
            }
            """.trimIndent()

    override suspend fun create(data: String, callbackId: String) {
        gameRepository?.let { repo ->
            val currentGamePackage = repo.currentGamePackage.value
            if (currentGamePackage != null) {
                // Create a copy of the game package with updated game state
                val updatedGamePackage = currentGamePackage.copy(
                    gameState = Json.parseToJsonElement(data).jsonObject,
                    state = com.rejnek.oog.data.model.GameState.IN_PROGRESS
                )

                // Update the current game package in the repository
                repo.updateCurrentGamePackage(updatedGamePackage)

                // Save to storage (this automatically updates library and marks as current saved game)
                repo.gameStorageRepository.saveGame(updatedGamePackage)
            }
        }
    }
}