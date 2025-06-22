package com.rejnek.oog.data.gameItems.direct

import com.rejnek.oog.data.gameItems.GenericDirectFactory

class ShowTask : GenericDirectFactory() {
    override val id = "showTask"

    override suspend fun create(data: String, callbackId: String) {
        val currentElementId = gameRepository?.currentElement?.value?.id ?: ""

        gameRepository?.setCurrentElement(data)
        gameRepository?.setElementVisible(data, true)

        gameRepository?.setElementVisible(currentElementId, false)
    }
}