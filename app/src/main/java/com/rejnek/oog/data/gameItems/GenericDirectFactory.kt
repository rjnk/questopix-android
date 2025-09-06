package com.rejnek.oog.data.gameItems

abstract class GenericDirectFactory : GenericItemFactory() {
    override val js: String
        get() =
            """
            function ${id}(...args) {
                directAction("$id", ...args);
            }
            """.trimIndent()
}


