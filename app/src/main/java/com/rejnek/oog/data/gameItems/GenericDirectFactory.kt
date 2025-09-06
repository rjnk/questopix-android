package com.rejnek.oog.data.gameItems

abstract class GenericDirectFactory : GenericItemFactory() {
    override val js: String
        get() =
            """
            function ${id}(...args) {
                directAction("$id", ...args);
            }
            """.trimIndent()

    open suspend fun create(data: String) {}

    open suspend fun createWithArgs(args: List<String>) {
        create(args.joinToString("\n"))
    }
}


