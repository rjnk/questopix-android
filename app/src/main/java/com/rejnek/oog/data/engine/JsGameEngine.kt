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
        try {
            if (isInitialized) {
                return@withContext Result.success(true)
            }

            Log.d("JsGameEngine", "Initializing WebView JavaScript engine...")

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
                        Log.d("JsGameEngine", "WebView page finished loading")
                    }
                }

                // Load HTML template to initialize the WebView with our persistent context
                loadDataWithBaseURL(null, htmlTemplate, "text/html", "UTF-8", null)
            }

            // Wait a bit to ensure WebView is properly initialized
            suspendCancellableCoroutine<Unit> { continuation ->
                Handler(Looper.getMainLooper()).postDelayed({
                    continuation.resume(Unit)
                }, 500)
            }

            isInitialized = true
            Log.d("JsGameEngine", "WebView JavaScript engine initialized successfully")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("JsGameEngine", "Error initializing WebView JavaScript engine", e)
            isInitialized = false
            Result.failure(e)
        }
    }

    /**
     * Evaluate JavaScript code using WebView.
     * Code is executed in the persistent context of the WebView.
     */
    override suspend fun evaluateJs(code: String): Result<String> = withContext(Dispatchers.Main) {
        try {
            if (!isInitialized) {
                val initResult = initialize()
                if (initResult.isFailure) {
                    return@withContext Result.failure(initResult.exceptionOrNull() ?: Exception("Failed to initialize JS engine"))
                }
            }

            val webViewInstance = webView ?: throw IllegalStateException("WebView is not initialized")

            Log.d("JsGameEngine", "Evaluating JavaScript code...")

            // Add wrapping to return the result
            val wrappedCode = "try { sendResult($code); } catch(e) { Android.debugPrint('JS Evaluation error: ' + e.message); 'ERROR: ' + e.message; }"

            // Use suspension to wait for JavaScript result
            val result = suspendCancellableCoroutine<String> { continuation ->
                webViewInstance.evaluateJavascript(wrappedCode) { resultValue ->
                    if (resultValue == "null" || resultValue == null) {
                        continuation.resume("null")
                    } else {
                        continuation.resume(resultValue)
                    }
                }
            }

            Log.d("JsGameEngine", "JavaScript evaluation result: $result")
            Result.success(cleanJsResult(result))

        } catch (e: Exception) {
            Log.e("JsGameEngine", "Error evaluating JavaScript", e)
            Result.failure(e)
        }
    }

    /**
     * Execute JavaScript code directly without wrapping it in sendResult
     * Useful for defining variables and functions
     */
    override suspend fun executeJs(code: String): Result<Unit> = withContext(Dispatchers.Main) {
        try {
            if (!isInitialized) {
                val initResult = initialize()
                if (initResult.isFailure) {
                    return@withContext Result.failure(initResult.exceptionOrNull() ?: Exception("Failed to initialize JS engine"))
                }
            }

            val webViewInstance = webView ?: throw IllegalStateException("WebView is not initialized")

            Log.d("JsGameEngine", "Executing JavaScript code...")

            // Execute code without expecting a result
            suspendCancellableCoroutine<Unit> { continuation ->
                webViewInstance.evaluateJavascript(code) {
                    continuation.resume(Unit)
                }
            }

            Log.d("JsGameEngine", "JavaScript executed")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("JsGameEngine", "Error executing JavaScript", e)
            Result.failure(e)
        }
    }

    /**
     * Clean the JavaScript result by removing quotes from strings
     */
    private fun cleanJsResult(result: String): String {
        // If result is a quoted string, remove the quotes
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
