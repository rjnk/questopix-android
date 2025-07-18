package com.rejnek.oog.data.gameItems.direct.commands

import com.rejnek.oog.data.gameItems.GenericDirectFactory

class SetSecondary : GenericDirectFactory() {
    override val id = "setSecondary"

    override suspend fun create(data: String, callbackId: String) {
        gameRepository?.setSecondaryTabElement(data)
    }
}