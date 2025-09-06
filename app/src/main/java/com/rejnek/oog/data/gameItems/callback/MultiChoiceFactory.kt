package com.rejnek.oog.data.gameItems.callback

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rejnek.oog.data.gameItems.GenericCallbackFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Usage:
 * multichoice("Kolik pater má Kauland", (answerNumber) => {
 *     if(answerNumber === 2) {
 *         debugPrint("ok");
 *         _score += 15;
 *     }
 *     finishGameButton("Konec");
 * }, "Jedno", "Dvě", "Tři");
 */
class MultiChoiceFactory : GenericCallbackFactory() {
    override val id: String = "multichoice"

    override suspend fun createWithArgs(args: List<String>, callbackId: String) {
        if (args.isEmpty()) return

        val questionText = args[0]
        val choices = args.drop(1) // All arguments after the first are choice options

        val multiChoice = MultiChoice(
            questionText = questionText,
            choices = choices,
            onChoiceSelected = { choiceIndex ->
                game?.resolveCallback(callbackId, choiceIndex.toString())
            }
        )

        gameRepository?.addUIElement { multiChoice.Show() }
    }
}

class MultiChoice(
    private val questionText: String,
    private val choices: List<String>,
    private val onChoiceSelected: (Int) -> Unit
) {
    private val _selectedChoice = MutableStateFlow<Int?>(null)
    val selectedChoice = _selectedChoice.asStateFlow()

    @Composable
    fun Show(
        modifier: Modifier = Modifier
    ) {
        val selectedChoice by this.selectedChoice.collectAsState()

        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
        ) {
            Text(
                text = questionText,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineSmall
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                choices.forEachIndexed { index, choice ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { _selectedChoice.value = index },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedChoice == index,
                            onClick = { _selectedChoice.value = index }
                        )
                        Text(
                            text = choice,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                                .padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Button(
                onClick = {
                    selectedChoice?.let { onChoiceSelected(it) }
                },
                enabled = selectedChoice != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Submit")
            }
        }
    }
}
