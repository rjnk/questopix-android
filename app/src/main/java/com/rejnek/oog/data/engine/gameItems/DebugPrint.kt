package com.rejnek.oog.data.engine.gameItems

import android.util.Log

class DebugPrint : GenericGameItem() {
    override val id = "debugPrint"
    override val js: String = """
        function debugPrint(message) {
            directAction("$id", message);
            return "";
        }
    """.trimIndent()

    override suspend fun run(data: String, callbackId: String) {
        Log.d("DebugPrint", "JS Debug: $data")
    }

    override fun clear() {
        // blank
    }
}