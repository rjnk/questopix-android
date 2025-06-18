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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    override suspend fun run(data: String, callbackId: String) {
        visible = true

        _questionState.value = QuestionState(data) { answer ->
            game?.resolveCallback(callbackId, answer)
        }
    }

    override fun clear() {
        visible = false
        _questionState.value = null
    }

    val _questionState = MutableStateFlow<QuestionState?>(null)
    val questionState: StateFlow<QuestionState?> = _questionState.asStateFlow()

    private val _answerText = MutableStateFlow("")
    val answerText = _answerText.asStateFlow()

    var visible = false


    @Composable
    fun Show(
        modifier: Modifier = Modifier
    ) {
        val answerText by this.answerText.collectAsState()
        val onValueChange: (String) -> Unit = {
            _answerText.value = it
        }
        val text = this.questionState.collectAsState().value?.questionText ?: "Err"
        val onSubmit: () -> Unit = {
            if (answerText.isNotBlank()) {
                this.questionState.value?.provideAnswer(answerText)
            }
        }

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