package com.rejnek.oog.data.gameItems.direct

import com.rejnek.oog.data.gameItems.GenericDirectFactory

class ShowTask : GenericDirectFactory() {
    override val id = "showTask"

    override suspend fun create(data: String, callbackId: String) {
        gameRepository?.setCurrentElement(data)
    }
}