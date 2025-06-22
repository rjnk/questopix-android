package com.rejnek.oog.data.gameItems.direct

import com.rejnek.oog.data.gameItems.GenericDirectFactory

class SetVisible : GenericDirectFactory() {
    override val id = "setVisible"

    override suspend fun create(data: String, callbackId: String) {
        gameRepository?.setElementVisible(data, true)
    }
}