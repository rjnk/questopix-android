package com.rejnek.oog.data.gameItems

class ShowTask : GenericGameFactory() {
    override val id = "showTask"
    override val js: String = """
        function showTask(elementId) {
            directAction("$id", elementId);
        }
    """.trimIndent()

    override suspend fun create(data: String, callbackId: String) {
        gameRepository?.setCurrentElement(data)
    }
}
