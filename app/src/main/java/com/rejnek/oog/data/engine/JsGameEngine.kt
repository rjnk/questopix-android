package com.rejnek.oog.data.engine

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import com.rejnek.oog.data.gameItems.GenericItemFactory
import com.rejnek.oog.data.model.Area
import com.rejnek.oog.data.model.Coordinates
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlin.coroutines.resume
import kotlin.text.toBoolean

class JsGameEngine(
    private val context: Context
) {
    // My variables
    private var repository: GameRepository? = null
    private val gameItems = arrayListOf<GenericItemFactory>()

    // WebView instance for JavaScript execution
    private var webView: WebView? = null
    var isInitialized = false

    // Interface for JavaScript to call Kotlin functions
    private val jsInterface = JsGameInterface(gameItems)

    /**
     * Initialize the JavaScript engine with WebView.
     * This needs to be called before any JavaScript evaluation.
     */
    @SuppressLint("SetJavaScriptEnabled")
    suspend fun initialize(
        gameRepository: GameRepository
    ): Result<Boolean> = withContext(Dispatchers.Main) {
        if (isInitialized) {
            return@withContext Result.success(true)
        }

        repository = gameRepository
        repository?.gameItemRepository?.getGameItemFactories()?.let { items ->
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

        // Update the WebView reference in jsInterface
        jsInterface.updateWebView(webView)

        isInitialized = true

        for (i in gameItems) {
            i.init(repository, jsInterface)
        }

        Result.success(true)
    }

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
        val result = evaluateJs(code, expectResult = true)

        return if(result.getOrNull() == "null") Result.failure(Exception("Value is null"))
        else result
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
            val webViewInstance = webView ?: return@withContext Result.failure(Exception("WebView not initialized"))

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

            Log.d("JsGameEngine", "JavaScript $code executed. Evaluation result: $result")
            Result.success(cleanJsResult(result))
        }

    suspend fun executeOnStart(elementId: String) {
        val onStartActivated = getJsValue("_onStartActivated.includes('$elementId')").getOrNull().toBoolean()

        if (!onStartActivated) {
            evaluateJs("$elementId.onStartFirst()")
            evaluateJs("if (!_onStartActivated.includes($elementId)) { _onStartActivated.push('$elementId'); }")
        }

        evaluateJs("$elementId.onStart()")
        evaluateJs("save()")
    }

    suspend fun restoreState(gameState: JsonObject) {
        evaluateJs("""
                const savedState = $gameState;
                Object.keys(savedState).forEach(key => {
                    if (key.startsWith('_')) {
                        globalThis[key] = savedState[key];
                    }
                });
            """.trimIndent())
    }

    private fun htmlTemplate(gameItems: List<GenericItemFactory>) = """
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
                    debugPrint("JS Error: " + message + " at " + source + ":" + lineno + ":" + colno + (error ? " Stack: " + error.stack : ""));
                    return true;
                };
                
                // Initialize callback resolvers storage
                window.callbackResolvers = {};
                
                // mandatory game variables
                var _onStartActivated = [];
                var _onEnterActivated = [];
                var _disabled = [];

                var _currentTask = "start";
                
                // Additional custom functions
                function showTask(newTask) {
                    _currentTask = newTask;
                    refresh();
                    save();
                }
                
                function enable(elementId) {
                    const index = _disabled.indexOf(elementId);
                    if (index !== -1) {
                        _disabled.splice(index, 1);
                    }
                    save();
                }
                
                function disable(elementId) {
                    if (!_disabled.includes(elementId)) {
                        _disabled.push(elementId);
                    }
                    save();
                }
                
                function isEnabled(elementId) {
                    return !_disabled.includes(elementId);
                }
                
                // Functions that interact with Android
                ${gameItems.joinToString("\n\n") { action -> action.js }}
            </script>
        </head>
        <body>
            <div id="output"></div>
        </body>
        </html>
    """.trimIndent()

    private fun cleanJsResult(result: String): String {
        return if (result.startsWith("\"") && result.endsWith("\"") && result.length >= 2) {
            result.substring(1, result.length - 1)
        } else {
            result
        }
    }

    fun cleanup() {
        Log.d("JsGameEngine", "Cleaning up WebView JavaScript engine resources")

        // First remove JS interface and update WebView reference in jsInterface
        webView?.removeJavascriptInterface("Android")
        jsInterface.updateWebView(null)

        // Properly destroy the WebView on the main thread
        webView?.post {
            webView?.stopLoading()
            webView?.clearHistory()
            webView?.clearCache(true)
            webView?.destroy()
            webView = null
        }

        // Reset other state variables
        isInitialized = false
        repository = null
        gameItems.clear()
    }

    suspend fun getArea(elementId: String): Area? {
        val length = getJsValue("$elementId.loc.length").getOrNull()?.toInt() ?: return null

        val coordinates = (0 until length).map { i ->
            val lat = getJsValue("$elementId.loc[$i][0]").getOrNull()?.toDouble() ?: return null
            val lng = getJsValue("$elementId.loc[$i][1]").getOrNull()?.toDouble() ?: return null
            Coordinates(lat, lng)
        }

        return Area(coordinates, elementId)
    }
}
