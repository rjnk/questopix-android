package com.rejnek.oog.data.gameItems.direct.commands

import android.util.Log
import com.rejnek.oog.data.gameItems.GenericDirectFactory

class DebugPrint : GenericDirectFactory() {
    override val id = "debugPrint"

    override suspend fun create(data: String, callbackId: String) {
        Log.d("DebugPrint", "JS Debug: $data")
    }
}