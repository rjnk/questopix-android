package com.rejnek.oog.data.gameItems.direct.commands

import com.rejnek.oog.data.gameItems.GenericDirectFactory

class ShowTask : GenericDirectFactory() {
    override val id = "showTask"

    override suspend fun create(data: String, callbackId: String) {
        val currentElementId = gameRepository?.selectedElement?.value?.id ?: ""

        // make the current element visible and hide the previous one
        gameRepository?.setCurrentElement(data)
        gameRepository?.jsEngine?.evaluateJs("if (!visibleTasks.includes('$data')) { visibleTasks.push('$data'); }")
        gameRepository?.jsEngine?.evaluateJs("visibleTasks = visibleTasks.filter(task => task !== '$currentElementId');")

        // If the current element is the secondary tab, update it
        if(gameRepository?.getSecondaryTabElementId() == currentElementId) {
            gameRepository?.jsEngine?.evaluateJs("secondaryTask = '$data';")
        }
    }
}