package com.rejnek.oog.engine

import com.rejnek.oog.engine.commands.GenericCommandFactory

/**
 * Generates an HTML template embedding JavaScript code
 * This is used to initialize the game environment in a WebView
 */
fun htmlTemplate(gameItems: List<GenericCommandFactory>) = """
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
                    disable(_currentTask);
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