package com.rejnek.oog.data.gameItems.direct.commands

import com.rejnek.oog.data.gameItems.GenericDirectFactory

class ShowTask : GenericDirectFactory() {
    override val id = "showTask"

    override val js: String
        get() =
            """
            function ${id}(newTask) {
                let previousTask = currentTask; 
    
                // visibleElements
                if (!visibleTasks.includes(newTask)) { visibleTasks.push(newTask); }
                visibleTasks = visibleTasks.filter(task => task !== previousTask);
               
                // If the current element is the secondary tab, update it
                if(secondaryTask === previousTask) {
                    secondaryTask = newTask;
                }
                
                currentTask = newTask;
                refresh();
            }
            """.trimIndent()

    override suspend fun create(data: String, callbackId: String) {
        // blank - the interaction is done in the refresh function
    }
}