package com.rejnek.oog.data.gameItems.direct.commands

import com.rejnek.oog.data.gameItems.GenericDirectFactory

class Refresh : GenericDirectFactory() {
    override val id = "refresh"

    override suspend fun create(data: String) {
        gameRepository?.refresh()
    }
}