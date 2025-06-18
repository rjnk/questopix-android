package com.rejnek.oog.data.engine

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.rejnek.oog.data.engine.gameItems.GenericGameItem
import com.rejnek.oog.data.engine.gameItems.InGameButton
import com.rejnek.oog.data.engine.gameItems.Question
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class JsGameEngine(
    private val context: Context
) {

    private var repository: GameRepository? = null

    // WebView instance for JavaScript execution
    private var webView: WebView? = null
    private var isInitialized = false

    // Interface for JavaScript to call Kotlin functions
    private val jsInterface = GameJsInterface()

    private val gameItems = arrayListOf<GenericGameItem>()

    /**
     * Initialize the JavaScript engine with WebView.
     * This should be called before any JavaScript evaluation.
     */
    @SuppressLint("SetJavaScriptEnabled")
    suspend fun initialize(
        gameRepository: GameRepository
    ): Result<Boolean> = withContext(Dispatchers.Main) {
        if (isInitialized) {
            return@withContext Result.success(true)
        }

        repository = gameRepository

        repository?.gameItems?.let { items ->
            gameItems.addAll(items.toList())
        }

        // Create WebView instance on the main thread
        webView = WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

            // Add JavaScript interface to enable JavaScript->Kotlin calls
            addJavascriptInterface(jsInterface, "Android")

            // Set WebViewClient to know when the page is loaded
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                }
            }

            loadDataWithBaseURL(null, htmlTemplate(gameItems), "text/html", "UTF-8", null)
        }

        isInitialized = true

        for( i in gameItems) {
            i.init(repository, jsInterface)
        }

        Result.success(true)
    }

    /**
     * Evaluate JavaScript code using WebView.
     * Code is executed in the persistent context of the WebView.
     * @param code The JavaScript code to evaluate
     * @param expectResult If true, wraps code to extract result; if false, returns empty string
     * @return Result containing the evaluation result as a string (empty if no result expected)
     */
    private suspend fun evaluateJs(code: String, expectResult: Boolean): Result<String> =
        withContext(Dispatchers.Main) {
            val webViewInstance = webView ?: throw IllegalStateException("WebView is not initialized")

            val actualCode = if (expectResult) "sendResult($code);" else code

            val result = suspendCancellableCoroutine<String> { continuation ->
                webViewInstance.evaluateJavascript(actualCode) { resultValue ->
                    continuation.resume(
                        when {
                            resultValue == "null" || resultValue == null -> "null"
                            else -> resultValue
                        }
                    )
                }
            }

            Log.d("JsGameEngine", "JavaScript executed. Evaluation result: $result")
            Result.success(cleanJsResult(result))
        }

    private fun htmlTemplate(gameItems: List<GenericGameItem>) = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <script type="text/javascript">
                // Global function to send results back to Kotlin
                function sendResult(result) {
                    return result;
                }
                
                // Error handler
                window.onerror = function(message, source, lineno, colno, error) {
                    Android.debugPrint("JS Error: " + message);
                    return true;
                };
                
                // Initialize callback resolvers storage
                window._callbackResolvers = {};
                
                // Generic function to create a callback and wait for its result
                async function createCallback(type, data) {
                    // Register the callback with Android
                    const callbackId = Android.registerCallback(type, data);
                    
                    // Return a promise that will be resolved when the callback is triggered
                    return new Promise((resolve) => {
                        // Store the resolver function that will be called when the callback is triggered
                        window._callbackResolvers[callbackId] = resolve;
                    });
                }
                
                // Function for direct actions that don't need to wait for user input
                function directAction(type, data) {
                    return Android.directAction(type, data || "");
                }
                
                ${gameItems.joinToString("\n\n") { action -> action.js }}
            </script>
        </head>
        <body>
            <div id="output"></div>
        </body>
        </html>
    """.trimIndent()

    /**
     * Evaluate JavaScript code without expecting a result.
     * This is useful for executing commands that do not return a value.
     * @param code The JavaScript code to evaluate
     * @return Result indicating success or failure of the evaluation
     */
    suspend fun evaluateJs(code: String): Result<Unit> {
        evaluateJs(code, expectResult = false)
        return Result.success(Unit)
    }

    /**
     * Evaluate JavaScript code and expect a result.
     * This is useful for getting values from the JavaScript context.
     * @param code The JavaScript code to evaluate
     * @return Result containing the evaluation result as a string
     */
    suspend fun getJsValue(code: String): Result<String> {
        return evaluateJs(code, expectResult = true)
    }

    /**
     * Clean the JavaScript result by removing quotes from strings
     */
    private fun cleanJsResult(result: String): String {
        return if (result.startsWith("\"") && result.endsWith("\"") && result.length >= 2) {
            result.substring(1, result.length - 1)
        } else {
            result
        }
    }

    // JavaScript interface class that serves as a bridge between JS and Kotlin
    inner class GameJsInterface {
        private val javaScriptCallbacks = mutableMapOf<String, (String) -> Unit>()
        private var callbackIdCounter = 0

        @JavascriptInterface
        fun showTask(elementId: String) {
            Log.d("JsGameEngine", "JS wants to show element: $elementId")

            CoroutineScope(Dispatchers.Main).launch {
                repository?.showTask(elementId)
            }
        }

        @JavascriptInterface
        fun debugPrint(message: String) {
            Log.d("JsGameEngine", "JS Debug: $message")
        }

        /**
         * Generic method for handling direct actions from JavaScript that don't need to wait for callbacks
         * @param actionType The type of action to perform (corresponds to game item ID)
         * @param data Additional data needed for the action
         * @return An immediate result string, if any (empty string for void actions)
         */
        @JavascriptInterface
        fun directAction(actionType: String, data: String): String {
            Log.d("JsGameEngine", "Direct action: $actionType with data: $data")

            val gameItem = gameItems.find { it.id == actionType }

            CoroutineScope(Dispatchers.Main).launch {
                gameItem?.run(data, "blank")
            }

            return ""
        }

        /**
         * Generic method to register a callback from JavaScript
         * @param callbackType The type of callback
         * @param data Additional data needed for the callback
         * @return A callback ID that JavaScript can use to resolve the callback
         */
        @JavascriptInterface
        fun registerCallback(callbackType: String, data: String): String {
            val callbackId = "callback_${callbackType}_${callbackIdCounter++}_${System.currentTimeMillis()}"
            Log.d("JsGameEngine", "Registering $callbackType callback: $callbackId with data: $data")

            CoroutineScope(Dispatchers.Main).launch {
                val gameItem = gameItems.find { it.id == callbackType }
                gameItem?.run(data, callbackId) ?: Log.e("JsGameEngine", "No game item found with ID: $callbackType")
            }

            return callbackId
        }

        /**
         * Called from JavaScript to wait for a callback to be resolved
         * @param callbackId The ID of the callback to wait for
         */
        @JavascriptInterface
        fun awaitCallback(callbackId: String) {

        }

        /**
         * Resolves a callback by its ID with the provided result
         * This is called by Kotlin when the callback action is completed
         */
        internal fun resolveCallback(callbackId: String, result: String) {
            Log.d("JsGameEngine", "Resolving callback: $callbackId with result: $result")

            // Resolve the promise in JavaScript with the result
            CoroutineScope(Dispatchers.Main).launch {
                val escapedResult = result.replace("'", "\\'")
                webView?.evaluateJavascript("""
                    if (window._callbackResolvers && window._callbackResolvers['$callbackId']) {
                        window._callbackResolvers['$callbackId']('$escapedResult');
                        delete window._callbackResolvers['$callbackId'];
                    }
                """.trimIndent(), null)
            }
        }
    }

    /**
     * Release resources when they're no longer needed
     */
    fun cleanup() {
        try {
            Log.d("JsGameEngine", "Cleaning up WebView JavaScript engine resources")
            webView?.destroy()
            webView = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e("JsGameEngine", "Error during cleanup", e)
        }
    }
}
