package com.rejnek.oog.data.gameItems

abstract class GenericCallbackFactory : GenericItemFactory() {
    override val js: String
        get() = """
            function ${id}(data, callback) {
                const callbackId = Android.registerCallback("$id", data);
                
                window.callbackResolvers[callbackId] = (result) => {
                    callback(result);
                    return "";
                };
            }
        """.trimIndent()

    open suspend fun create(data: String, callbackId: String) { }

    open suspend fun createWithArgs(args: List<String>, callbackId: String) {
        create(args.joinToString("\n"), callbackId)
    }
}