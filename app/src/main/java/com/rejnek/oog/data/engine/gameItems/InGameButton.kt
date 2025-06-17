package com.rejnek.oog.data.engine.gameItems

class InGameButton() : GenericGameItem() {
    override val id = "button"
    override val js = """
        function button(text, callback) {
            // Register a button callback
            const callbackId = Android.registerCallback("button", text);
                    
            // Store the user's callback to be executed when the button is clicked
            window._callbackResolvers[callbackId] = () => {
                callback();
                return ""; // Buttons don't return a value
            };
        }
    """.trimIndent()

    override suspend fun run(data: String, callbackId: String) {
        gameRepository?.addButton(data) {
            game?.resolveCallback(callbackId, "")
        }
    }
}