package com.rejnek.oog.engine.commands.direct.simple

import com.rejnek.oog.engine.commands.GenericDirectFactory

class Refresh : GenericDirectFactory() {
    override val id = "refresh"

    override suspend fun create(data: String) {
        gameRepository?.refresh()
    }
}