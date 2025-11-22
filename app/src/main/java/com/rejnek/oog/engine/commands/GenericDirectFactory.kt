package com.rejnek.oog.engine.commands

/**
 * Abstract class representing a direct command factory.
 * The commands inheriting from this class are fire and forget - they do not expect any response from the app side.
 */
abstract class GenericDirectFactory : GenericCommandFactory() {
    override val js: String
        get() =
            """
            function ${id}(...args) {
                Android.directAction("$id", args.map(arg => String(arg)));
            }
            """.trimIndent()

    open suspend fun create(data: String) {}

    open suspend fun createWithArgs(args: List<String>) {
        create(args.joinToString("\n"))
    }
}
