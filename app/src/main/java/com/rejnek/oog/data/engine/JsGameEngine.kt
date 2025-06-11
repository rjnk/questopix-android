package com.rejnek.oog.data.engine

import android.content.Context
import android.util.Log
import androidx.javascriptengine.JavaScriptIsolate
import androidx.javascriptengine.JavaScriptSandbox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class JsGameEngine(private val context: Context) {

    // Keep references to sandbox and isolate for persistent usage
    private var jsSandbox: JavaScriptSandbox? = null
    private var jsIsolate: JavaScriptIsolate? = null
    private var isInitialized = false

    /**
     * Initialize the JavaScript engine.
     * This should be called before any JavaScript evaluation.
     */
    suspend fun initialize(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (isInitialized) {
                return@withContext Result.success(true)
            }

            Log.d("JsGameEngine", "Initializing JavaScript engine on background thread...")

            // Try with timeout using coroutines
            jsSandbox = withTimeoutOrNull(15000L) {
                val jsSandboxFuture = JavaScriptSandbox.createConnectedInstanceAsync(context)
                jsSandboxFuture.get()
            } ?: throw TimeoutException("JavaScript sandbox initialization timed out")

            Log.d("JsGameEngine", "JavaScript sandbox initialized successfully")

            // Create isolate
            jsIsolate = jsSandbox?.createIsolate()
            Log.d("JsGameEngine", "JavaScript isolate created")

            isInitialized = true
            Result.success(true)
        } catch (e: Exception) {
            Log.e("JsGameEngine", "Error initializing JavaScript engine", e)
            isInitialized = false
            Result.failure(e)
        }
    }

    /**
     * Evaluate JavaScript code using the persistent isolate.
     */
    suspend fun evaluateJs(code: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!isInitialized) {
                val initResult = initialize()
                if (initResult.isFailure) {
                    return@withContext Result.failure(initResult.exceptionOrNull() ?: Exception("Failed to initialize JS engine"))
                }
            }

            val isolate = jsIsolate ?: throw IllegalStateException("JavaScript isolate is not initialized")

            Log.d("JsGameEngine", "Evaluating JavaScript code...")
            val result = isolate.evaluateJavaScriptAsync(code).get(5, TimeUnit.SECONDS)
            Log.d("JsGameEngine", "JavaScript evaluation result: $result")

            Result.success(result.toString())
        } catch (e: Exception) {
            Log.e("JsGameEngine", "Error evaluating JavaScript", e)
            Result.failure(e)
        }
    }

    /**
     * Simple calculation example for testing
     */
    suspend fun calculateExample(n: Int): Result<String> {
        val code = "function sum(a, b) { return (a * b).toString(); }; sum($n, 6)"
        return evaluateJs(code)
    }

    /**
     * Release resources when they're no longer needed
     */
    fun cleanup() {
        try {
            Log.d("JsGameEngine", "Cleaning up JavaScript engine resources")
            jsIsolate?.close()
            jsIsolate = null
            jsSandbox = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e("JsGameEngine", "Error during cleanup", e)
        }
    }
}