package com.rejnek.oog.data.engine

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.rejnek.oog.data.repository.GameRepositoryInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class JsGameEngine(
    private val gameRepository: GameRepositoryInterface? = null,
    private val context: Context
) : JsEngineInterface {

    // WebView instance for JavaScript execution
    private var webView: WebView? = null
    private var isInitialized = false

    // Interface for JavaScript to call Kotlin functions
    private val jsInterface = GameJsInterface()

    // Setter for the callback to avoid circular dependencies
    fun setCallback(newCallback: GameRepositoryInterface) {
        val field = JsGameEngine::class.java.getDeclaredField("gameRepository")
        field.isAccessible = true
        field.set(this, newCallback)
    }

    // HTML template for WebView to provide persistent JS context
    private val htmlTemplate = """
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
                
                // Global debug function that links to Kotlin
                function debugPrint(message) {
                    Android.debugPrint(message);
                }
                
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
                
                // Global button function to create UI buttons
                function button(text, callback) {
                    // Register a button callback
                    const callbackId = Android.registerCallback("button", text);
                    
                    // Store the user's callback to be executed when the button is clicked
                    window._callbackResolvers[callbackId] = () => {
                        callback();
                        return ""; // Buttons don't return a value
                    };
                }
                
                // Global question function as async - will return a promise automatically
                async function question(questionText) {
                    // Use our generic callback system to handle the question
                    return await createCallback("question", questionText);
                }
            </script>
        </head>
        <body>
            <div id="output"></div>
        </body>
        </html>
    """.trimIndent()

    /**
     * Initialize the JavaScript engine with WebView.
     * This should be called before any JavaScript evaluation.
     */
    @SuppressLint("SetJavaScriptEnabled")
    override suspend fun initialize(): Result<Boolean> = withContext(Dispatchers.Main) {
        if (isInitialized) {
            return@withContext Result.success(true)
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

            loadDataWithBaseURL(null, htmlTemplate, "text/html", "UTF-8", null)
        }

        isInitialized = true
        Result.success(true)
    }

    /**
     * Evaluate JavaScript code using WebView.
     * Code is executed in the persistent context of the WebView.
     * @param code The JavaScript code to evaluate
     * @param expectResult If true, wraps code to extract result; if false, returns empty string
     * @return Result containing the evaluation result as a string (empty if no result expected)
     */
    private suspend fun evaluateJs(code: String, expectResult: Boolean): Result<String> = withContext(Dispatchers.Main) {
        val webViewInstance = webView ?: throw IllegalStateException("WebView is not initialized")

        // Add wrapping to return the result if needed
        val actualCode = if (expectResult) {
            "sendResult($code);"
        } else {
            code
        }

        // Use suspension to wait for JavaScript execution
        val result = suspendCancellableCoroutine<String> { continuation ->
            webViewInstance.evaluateJavascript(actualCode) { resultValue ->
                continuation.resume(when {
                    resultValue == "null" || resultValue == null -> "null"
                    else -> resultValue
                })
            }
        }

        Log.d("JsGameEngine", "JavaScript executed. Evaluation result: $result")

        Result.success(cleanJsResult(result))
    }

    /**
     * Evaluate JavaScript code without expecting a result.
     * This is useful for executing commands that do not return a value.
     * @param code The JavaScript code to evaluate
     * @return Result indicating success or failure of the evaluation
     */
    override suspend fun evaluateJs(code: String): Result<Unit> {
        evaluateJs(code, expectResult = false)
        return Result.success(Unit)
    }

    /**
     * Evaluate JavaScript code and expect a result.
     * This is useful for getting values from the JavaScript context.
     * @param code The JavaScript code to evaluate
     * @return Result containing the evaluation result as a string
     */
    override suspend fun getJsValue(code: String): Result<String> {
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
        private var pendingQuestion: String? = null
        private val javaScriptCallbacks = mutableMapOf<String, (String) -> Unit>()
        private var callbackIdCounter = 0

        @JavascriptInterface
        fun debugPrint(message: String) {
            Log.d("JsGameEngine", "JS Debug: $message")
        }

        @JavascriptInterface
        fun showTask(elementId: String) {
            Log.d("JsGameEngine", "JS wants to show element: $elementId")

            CoroutineScope(Dispatchers.Main).launch {
                gameRepository?.showTask(elementId)
            }
        }

        /**
         * Generic method to register a callback from JavaScript
         * @param callbackType The type of callback (e.g., "button", "question")
         * @param data Additional data needed for the callback (e.g., button text, question text)
         * @return A callback ID that JavaScript can use to resolve the callback
         */
        @JavascriptInterface
        fun registerCallback(callbackType: String, data: String): String {
            val callbackId = "callback_${callbackType}_${callbackIdCounter++}_${System.currentTimeMillis()}"
            Log.d("JsGameEngine", "Registering $callbackType callback: $callbackId with data: $data")

            CoroutineScope(Dispatchers.Main).launch {
                when (callbackType) {
                    "button" -> {
                        gameRepository?.addButton(data) {
                            resolveCallback(callbackId, "")
                        }
                    }
                    "question" -> {
                        pendingQuestion = data
                        gameRepository?.showQuestion(data) { answer ->
                            resolveCallback(callbackId, answer)
                        }
                    }
                }
            }

            return callbackId
        }

        /**
         * Called from JavaScript to wait for a callback to be resolved
         * @param callbackId The ID of the callback to wait for
         */
        @JavascriptInterface
        fun awaitCallback(callbackId: String) {
            Log.d("JsGameEngine", "JavaScript is waiting for callback: $callbackId")
            // No action needed here - JavaScript will create a Promise that resolves when resolveCallback is called
        }

        /**
         * Resolves a callback by its ID with the provided result
         * This is called by Kotlin when the callback action is completed
         */
        private fun resolveCallback(callbackId: String, result: String) {
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
    override fun cleanup() {
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
