package com.rejnek.oog.data.gameItems.direct.commands

import android.util.Log
import com.rejnek.oog.data.gameItems.GenericDirectFactory

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
        Log.d("Save", "Saving game state: $data")
    }
}