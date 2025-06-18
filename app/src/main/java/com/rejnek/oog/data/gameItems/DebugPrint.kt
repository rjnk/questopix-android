package com.rejnek.oog.data.gameItems

import android.util.Log

class DebugPrint : GenericGameFactory() {
    override val id = "debugPrint"
    override val js: String = """
        function debugPrint(message) {
            directAction("$id", message);
        }
    """.trimIndent()

    override suspend fun create(data: String, callbackId: String) {
        Log.d("DebugPrint", "JS Debug: $data")
    }
}