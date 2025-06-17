package com.rejnek.oog.data.engine

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class JsGameEngine(
    private val callback: GameEngineCallback? = null,
    private val context: Context
) {

    // WebView instance for JavaScript execution
    private var webView: WebView? = null
    private var isInitialized = false

    // Interface for JavaScript to call Kotlin functions
    private val jsInterface = GameJsInterface()

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
                
                // Global function to show an element
                function showElement(elementId) {
                    Android.showElement(elementId);
                }
                
                // Global question function as async - will return a promise automatically
                async function question(questionText) {
                    // Simply return a promise that will be resolved by Android
                    return new Promise((resolve) => {
                        // Store the resolver globally so Android can access it
                        window._questionResolver = resolve;
                        // Notify Android about the question
                        Android.notifyQuestion(questionText);
                    });
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
    suspend fun initialize(): Result<Boolean> = withContext(Dispatchers.Main) {
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
    suspend fun evaluateJs(code: String): Result<String> = withContext(Dispatchers.Main) {
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
    suspend fun executeJs(code: String): Result<Unit> = withContext(Dispatchers.Main) {
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

        @JavascriptInterface
        fun debugPrint(message: String) {
            Log.d("JsGameEngine", "JS Debug: $message")
        }

        @JavascriptInterface
        fun showElement(elementId: String) {
            Log.d("JsGameEngine", "JS wants to show element: $elementId")

            CoroutineScope(Dispatchers.Main).launch {
                callback?.showElement(elementId)
            }
        }

        @JavascriptInterface
        fun notifyQuestion(question: String) {
            Log.d("JsGameEngine", "JS is asking question: $question")
            pendingQuestion = question

            // Here you would trigger your UI to show the question to the user
            // For testing purposes, we'll simulate a delayed answer
            CoroutineScope(Dispatchers.Main).launch {
                // Simulate delay for user thinking and answering
                delay(6000L)

                // Provide the answer (this would come from your UI in a real app)
                provideQuestionAnswer("buk")
            }
        }

        /**
         * Call this method when you have the answer from the user
         */
        fun provideQuestionAnswer(answer: String) {
            if (pendingQuestion == null) {
                Log.w("JsGameEngine", "Trying to answer a question that wasn't asked")
                return
            }

            Log.d("JsGameEngine", "Providing answer: $answer for question: $pendingQuestion")
            pendingQuestion = null

            // Resolve the promise in JavaScript with the answer
            CoroutineScope(Dispatchers.Main).launch {
                webView?.evaluateJavascript("""
                    if (window._questionResolver) {
                        window._questionResolver("$answer");
                        window._questionResolver = null;
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
