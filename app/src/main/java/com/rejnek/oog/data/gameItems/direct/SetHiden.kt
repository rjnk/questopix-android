package com.rejnek.oog.data.gameItems.direct

import com.rejnek.oog.data.gameItems.GenericDirectFactory

class SetHiden : GenericDirectFactory() {
    override val id = "setHiden"

    override suspend fun create(data: String, callbackId: String) {
        gameRepository?.setElementVisible(data, false)
    }
}