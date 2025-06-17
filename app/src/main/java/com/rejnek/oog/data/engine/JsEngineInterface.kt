package com.rejnek.oog.data.engine

/**
 * Interface for JavaScript engine operations
 * This allows for decoupling the GameRepository from the specific JS engine implementation
 */
interface JsEngineInterface {
    /**
     * Initialize the JavaScript engine
     * @return Result indicating success or failure of initialization
     */
    suspend fun initialize(): Result<Boolean>

    /**
     * Evaluate JavaScript code and return the result
     * @param code The JavaScript code to evaluate
     * @param expectResult If true, wraps code to extract result; if false, returns empty string
     * @return Result containing the evaluation result as a string (empty if no result expected)
     */
    suspend fun evaluateJs(code: String): Result<Unit>


    suspend fun getJsValue(code: String): Result<String>

    /**
     * Release resources when they're no longer needed
     */
    fun cleanup()
}
