package com.rejnek.oog.data.engine.gameItems

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Question() : GenericGameItem() {
    override val id: String = "question"
    override val js: String = """
        async function question(questionText) {
        return await createCallback("$id", questionText);
        }
    """.trimIndent()

    val _questionState = MutableStateFlow<QuestionState?>(null)
    val questionState: StateFlow<QuestionState?> = _questionState.asStateFlow()

    override suspend fun run(data: String, callbackId: String) {
        gameRepository?.showQuestion(data) { answer ->
            game?.resolveCallback(callbackId, answer)
        }
    }

    override fun clear() {
        _questionState.value = null
    }

    @Composable
    fun Show(
        text: String,
        answerText: String,
        onValueChange: (String) -> Unit,
        onSubmit: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = answerText,
            onValueChange = onValueChange,
            label = { Text("Your answer") },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSubmit,
        ) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Class representing a question that needs to be answered
 */
data class QuestionState(
    val questionText: String,
    val provideAnswer: (String) -> Unit
)