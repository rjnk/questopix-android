package com.rejnek.oog.engine.commands.direct.simple

import com.rejnek.oog.engine.commands.GenericDirectFactory

class DebugPrint : GenericDirectFactory() {
    override val id = "debugPrint"

    override suspend fun create(data: String) {
        // we print to log all direct actions, so this is duplicate
        // Log.d("DebugPrint", "JS Debug: $data")
    }
}