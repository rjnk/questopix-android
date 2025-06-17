package com.rejnek.oog.data.engine.gameItems

class Question() : GenericGameItem() {
    override val id: String = "question"
    override val js: String = """
        async function question(questionText) {
        return await createCallback("$id", questionText);
        }
    """.trimIndent()

    override suspend fun run(data: String, callbackId: String) {
        gameRepository?.showQuestion(data) { answer ->
            game?.resolveCallback(callbackId, answer)
        }
    }
}