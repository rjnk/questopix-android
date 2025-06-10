package com.rejnek.oog.data.engine

import android.util.Log
import com.rejnek.oog.data.model.Coordinates
import com.rejnek.oog.data.model.Game
import com.rejnek.oog.data.model.GameElement
import com.rejnek.oog.data.model.GameElementType
import com.rejnek.oog.data.repository.GameRepository
import org.mozilla.javascript.BaseFunction
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.Undefined

class JavaScriptGameEngine(
    private val gameRepository: GameRepository
) {
    private var jsContext: Context? = null
    private var scope: Scriptable? = null

    fun initialize(gameScript: String): Result<Unit> {
        return try {
            jsContext = Context.enter()
            jsContext?.optimizationLevel = -1 // Disable optimization for Android compatibility
            
            scope = jsContext?.initStandardObjects()

            // Add game API functions to JavaScript context
//            addGameApiFunctions(gameRepository.currentGame.value)
            
            // Execute the game script
            jsContext?.evaluateString(scope, gameScript, "game.js", 1, null)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    private fun addGameApiFunctions(gameContext: Game?) {
//        scope?.let { scope ->
//            // Create a game API object that can be accessed from JavaScript
//            val gameAPI = object : ScriptableObject() {
//                override fun getClassName(): String = "GameAPI"
//            }
//
//            // Make gameAPI available globally
//            ScriptableObject.defineProperty(scope, "gameAPI", gameAPI, ScriptableObject.DONTENUM)
//
//            // Create nextElement function to change the current game element
//            val nextElementFunction = object : BaseFunction() {
//                override fun call(
//                    cx: Context?,
//                    scope: Scriptable?,
//                    thisObj: Scriptable?,
//                    args: Array<out Any>?
//                ): Any {
//                    if (args != null && args.isNotEmpty()) {
//                        val elementName = args[0].toString()
//                        Log.d("JavaScriptGameEngine", "nextElement called with: $elementName")
//                        currentGameRef?.let { game ->
//                            // Find the requested element
//                            val nextElement = game.elements.find { it.id == elementName }
//                            if (nextElement != null) {
//                                // Update the current game reference with a new Game object that has the updated currentElement
//                                currentGameRef = game.copy(currentElement = nextElement)
//                                Log.d("JavaScriptGameEngine", "Current element changed to: ${nextElement.name}")
//                            } else {
//                                Log.e("JavaScriptGameEngine", "Element not found: $elementName")
//                            }
//                        }
//                    }
//                    return Undefined.instance
//                }
//
//                override fun getFunctionName(): String = "nextElement"
//            }
//
//            // Define the nextElement function in the global scope
//            ScriptableObject.defineProperty(scope, "nextElement", nextElementFunction, ScriptableObject.DONTENUM)
//        }
//    }
//
//    fun executeOnContinue(element: GameElement, gameContext: Game): Result<Unit> {
//        Log.d("JavaScriptGameEngine", "Executing onContinue for element: ${element.name}")
//        return element.onContinueScript?.let { script ->
//            executeScript(script, gameContext)
//        } ?: Result.success(Unit)
//    }

//    private fun executeScript(script: String, gameContext: Game): Result<Unit> {
//        return try {
//            jsContext?.evaluateString(scope, script, "inline", 1, null)
//            Log.d("JavaScriptGameEngine", "Executed script: $script")
//            // Update the current game reference after executing the script
//            currentGameRef = gameContext.copy(
//                currentElement = gameContext.currentElement // Assuming the script might change the current element
//            )
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

    fun getElementFromScope(elementName: String): GameElement? {
        return try {
            scope?.let { scope ->
                val obj = ScriptableObject.getProperty(scope, elementName)
                if (obj != Scriptable.NOT_FOUND) {
                    convertJSObjectToGameElement(obj as Scriptable, elementName)
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun convertJSObjectToGameElement(jsObj: Scriptable, name: String): GameElement {
        val objName = ScriptableObject.getProperty(jsObj, "name")?.toString() ?: name
        val description = ScriptableObject.getProperty(jsObj, "description")?.toString() ?: ""
        val type = ScriptableObject.getProperty(jsObj, "type")?.toString()
        
        // Extract coordinates
        val coordinates = ScriptableObject.getProperty(jsObj, "coordinates")?.let { coordObj ->
            if (coordObj is Scriptable) {
                val lat = ScriptableObject.getProperty(coordObj, "lat") as? Double ?: 0.0
                val lng = ScriptableObject.getProperty(coordObj, "lng") as? Double ?: 0.0
                val radius = ScriptableObject.getProperty(coordObj, "radius") as? Double ?: 25.0
                Coordinates(lat, lng, radius)
            } else null
        }
        
        val onContinueScript = ScriptableObject.getProperty(jsObj, "onContinue")?.let { func ->
            if (func is Function) {
                "${name}.onContinue()"
            } else null
        }
        
        // Determine element type
        val elementType = when {
            type == "start" -> GameElementType.START
            type == "navigation" -> GameElementType.NAVIGATION
            type == "finish" -> GameElementType.FINISH
            else -> GameElementType.TASK
        }

        Log.d("JavaScriptGameEngine", "Converted JS object to GameElement: $objName")
        Log.d("JavaScriptGameEngine", "Object properties - " +
                "Description: $description, Type: $type, Coordinates: $coordinates, " +
                "OnContinue: $onContinueScript")
        
        return GameElement(
            id = name,
            name = objName,
            elementType = elementType,
            visible = false,
            coordinates = coordinates ?: Coordinates(0.0, 0.0),
            description = description,
            onContinueScript = onContinueScript
        )
    }

    fun cleanup() {
        jsContext?.let { Context.exit() }
        jsContext = null
        scope = null
    }
}
