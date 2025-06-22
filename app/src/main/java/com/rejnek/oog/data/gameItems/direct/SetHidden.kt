package com.rejnek.oog.data.gameItems.direct

import com.rejnek.oog.data.gameItems.GenericDirectFactory

class SetHidden : GenericDirectFactory() {
    override val id = "setHidden"

    override suspend fun create(data: String, callbackId: String) {
        gameRepository?.setElementVisible(data, false)
    }
}