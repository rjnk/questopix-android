package com.rejnek.oog.data.engine

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.rejnek.oog.data.gameItems.GenericGameFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * JavaScript interface class that serves as a bridge between JS and Kotlin
 */
class JsGameInterface(
    private val gameItems: ArrayList<GenericGameFactory>,
    private var webView: WebView? = null
) {
    private val javaScriptCallbacks = mutableMapOf<String, (String) -> Unit>()
    private var callbackIdCounter = 0

    /**
     * Generic method for handling direct actions from JavaScript that don't need to wait for callbacks
     * @param actionType The type of action to perform (corresponds to game item ID)
     * @param data Additional data needed for the action
     * @return An immediate result string, if any (empty string for void actions)
     */
    @JavascriptInterface
    fun directAction(actionType: String, data: String): Unit {
        Log.d("JsGameEngine", "Direct action: $actionType with data: $data")

        val gameItem = gameItems.find { it.id == actionType }

        CoroutineScope(Dispatchers.Main).launch {
            gameItem?.create(data, "blank")
        }
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
            gameItem?.create(data, callbackId) ?: Log.e("JsGameEngine", "No game item found with ID: $callbackType")
        }

        return callbackId
    }

    /**
     * Resolves a callback by its ID with the provided result
     * This is called by Kotlin when the callback action is completed
     */
    public fun resolveCallback(callbackId: String, result: String) {
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

    /**
     * Update the WebView reference
     */
    internal fun updateWebView(newWebView: WebView?) {
        webView = newWebView
    }
}
