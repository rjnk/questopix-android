package com.rejnek.oog.data.gameItems

abstract class GenericCallbackFactory : GenericItemFactory() {
    override val js: String
        get() = """
            function ${id}(data, callback, ...args) {
                const callbackId = Android.registerCallback("$id", [data, ...args.map(arg => String(arg))]);
                
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