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
     * Execute JavaScript code without expecting a return value
     * @param code The JavaScript code to execute
     * @return Result indicating success or failure of execution
     */
    suspend fun executeJs(code: String): Result<Unit>

    /**
     * Evaluate JavaScript code and return the result
     * @param code The JavaScript code to evaluate
     * @return Result containing the evaluation result as a string
     */
    suspend fun evaluateJs(code: String): Result<String>

    /**
     * Release resources when they're no longer needed
     */
    fun cleanup()
}
