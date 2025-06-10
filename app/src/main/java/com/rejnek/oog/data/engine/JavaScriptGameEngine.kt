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
            
            // Define console functions for JavaScript
            defineConsoleFunctions()

            // Execute the game script
            jsContext?.evaluateString(scope, gameScript, "game.js", 1, null)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun defineConsoleFunctions() {
        scope?.let { scope ->
            // Define consolePrint function
            val consolePrint = object : BaseFunction() {
                override fun call(
                    cx: Context,
                    scope: Scriptable,
                    thisObj: Scriptable,
                    args: Array<Any>
                ): Any {
                    val message = if (args.isNotEmpty()) args[0].toString() else ""
                    Log.d("GameScript", message)
                    return Undefined.instance
                }
            }
            val showElement = object : BaseFunction() {
                override fun call(
                    cx: Context,
                    scope: Scriptable,
                    thisObj: Scriptable,
                    args: Array<Any>
                ): Any {
                    val nextElementName = if (args.isNotEmpty()) args[0].toString() else ""

                    Log.d("GameScript", "Showing element: $nextElementName")

                    gameRepository.setCurrentElement(nextElementName)
                    return Undefined.instance
                }
            }

            ScriptableObject.putProperty(scope, "consolePrint", consolePrint)
            ScriptableObject.putProperty(scope, "showElement", showElement)

            // We could add more console functions here in the future
        }
    }

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

    fun executeOnContinue(element: GameElement?): Result<Unit> {
        if (element == null) return Result.failure(Exception("Element is null"))

        return try {
            element.onContinueScript?.let { scriptToExecute ->
                Log.d("JavaScriptGameEngine", "Executing script: $scriptToExecute")
                jsContext?.evaluateString(scope, scriptToExecute, "onContinue", 1, null)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("JavaScriptGameEngine", "Error executing onContinue", e)
            Result.failure(e)
        }
    }
}
