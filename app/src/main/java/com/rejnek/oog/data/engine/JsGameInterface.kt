package com.rejnek.oog.data.engine

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.rejnek.oog.data.gameItems.GenericCallbackFactory
import com.rejnek.oog.data.gameItems.GenericDirectFactory
import com.rejnek.oog.data.gameItems.GenericItemFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * JavaScript interface class that serves as a bridge between JS and Kotlin
 */
class JsGameInterface(
    private val gameItems: ArrayList<GenericItemFactory>,
    private var webView: WebView? = null
) {
    private val javaScriptCallbacks = mutableMapOf<String, (String) -> Unit>()
    private var callbackIdCounter = 0

    /**
     * Generic method for handling direct actions from JavaScript that don't need to wait for callbacks
     * @param actionType The type of action to perform (corresponds to game item ID)
     * @param args Additional arguments needed for the action
     * @return An immediate result string, if any (empty string for void actions)
     */
    @JavascriptInterface
    fun directAction(actionType: String, vararg args: String): Unit {
        Log.d("JsGameEngine", "Direct action: $actionType with args: ${args.joinToString(", ")}")

        val gameItem = gameItems.find { it.id == actionType } as GenericDirectFactory

        CoroutineScope(Dispatchers.Main).launch {
            gameItem.createWithArgs(args.toList())
        }
    }

    /**
     * Generic method to register a callback from JavaScript
     * @param callbackType The type of callback
     * @param args Additional arguments needed for the callback
     * @return A callback ID that JavaScript can use to resolve the callback
     */
    @JavascriptInterface
    fun registerCallback(callbackType: String, vararg args: String): String {
        val callbackId = "callback_${callbackType}_${callbackIdCounter++}_${System.currentTimeMillis()}"
        Log.d("JsGameEngine", "Registering $callbackType callback: $callbackId with args: ${args.joinToString(", ")}")

        val gameItem = gameItems.find { it.id == callbackType } as GenericCallbackFactory

        CoroutineScope(Dispatchers.Main).launch {
            gameItem.createWithArgs(args.toList(), callbackId)
        }

        return callbackId
    }

    /**
     * Resolves a callback by its ID with the provided result
     * This is called by Kotlin when the callback action is completed
     */
    fun resolveCallback(callbackId: String, result: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val escapedResult = result.replace("'", "\\'")
            webView?.evaluateJavascript("""
                if (window.callbackResolvers && window.callbackResolvers['$callbackId']) {
                    window.callbackResolvers['$callbackId']('$escapedResult');
                    // delete window.callbackResolvers['$callbackId'];
                }
            """.trimIndent(), null)
        }
    }

    /**
     * Deletes all registered callbacks in the WebView context
     * This is useful for cleaning up when the game ends or resets
     */
    fun deleteAllFallbacks() {
        CoroutineScope(Dispatchers.Main).launch {
            webView?.evaluateJavascript("""
                if (window.callbackResolvers) {
                    window.callbackResolvers = {};
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
