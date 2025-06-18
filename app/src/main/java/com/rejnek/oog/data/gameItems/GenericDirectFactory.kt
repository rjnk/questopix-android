package com.rejnek.oog.data.gameItems

abstract class GenericDirectFactory : GenericGameFactory() {
    override val js: String
        get() =
            """
            function ${id}(elementId) {
                directAction("$id", elementId);
            }
            """.trimIndent()
}


