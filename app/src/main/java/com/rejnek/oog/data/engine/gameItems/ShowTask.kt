package com.rejnek.oog.data.engine.gameItems

import android.util.Log

class ShowTask : GenericGameItem() {
    override val id = "showTask"
    override val js: String = """
        function showTask(elementId) {
            directAction("$id", elementId);
        }
    """.trimIndent()

    override suspend fun run(data: String, callbackId: String) {
        gameRepository?.showTask(data)
    }

    override fun clear() {
        // blank
    }
}
