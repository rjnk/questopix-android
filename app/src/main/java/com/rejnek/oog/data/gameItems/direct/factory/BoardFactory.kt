package com.rejnek.oog.data.gameItems.direct.factory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rejnek.oog.data.gameItems.GenericDirectFactory

// usage: board("Výsledky", "Scóre", _score, "Čas", elapsedMinutes + "min.")
class BoardFactory : GenericDirectFactory() {
    override val id = "board"

    override suspend fun createWithArgs(args: List<String>) {
        val title = args[0]
        val pairs = mutableListOf<Pair<String, String>>()

        // Parse pairs of label-value from remaining arguments
        for (i in 1 until args.size step 2) {
            if (i + 1 < args.size) {
                pairs.add(Pair(args[i], args[i + 1]))
            }
        }

        gameRepository?.addUIElement {
            MyBoard(title = title, items = pairs).Show()
        }
    }
}

class MyBoard(
    private val title: String,
    private val items: List<Pair<String, String>>
) {
    @Composable
    fun Show() {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Items in two columns
                items.forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label,)
                        Text(value,)
                    }
                }
            }
        }
    }
}
