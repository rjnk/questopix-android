package com.rejnek.oog.engine.commands.direct.simple

import com.rejnek.oog.engine.commands.GenericDirectFactory

/**
 * This command re-sets the current task. This results in redrawing the UI.
 */
class Refresh : GenericDirectFactory() {
    override val id = "refresh"

    override suspend fun create(data: String) {
        gameRepository?.refresh()
    }
}