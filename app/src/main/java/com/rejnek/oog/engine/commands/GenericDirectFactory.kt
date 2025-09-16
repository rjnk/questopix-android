package com.rejnek.oog.engine.commands

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


