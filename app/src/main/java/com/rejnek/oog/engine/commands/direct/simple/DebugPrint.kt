package com.rejnek.oog.engine.commands.direct.simple

import com.rejnek.oog.engine.commands.GenericDirectFactory

/**
 * This commands prints a message to the log.
 * As we print all direct actions, the implementation is empty.
 * You can still call this command within JavaScript and the value will display in the log.
 */
class DebugPrint : GenericDirectFactory() {
    override val id = "debugPrint"

    override suspend fun create(data: String) {
        // we print to log all direct actions, so this is duplicate
        // Log.d("DebugPrint", "JS Debug: $data")
    }
}