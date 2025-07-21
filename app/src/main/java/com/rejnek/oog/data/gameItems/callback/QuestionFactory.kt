package com.rejnek.oog.data.gameItems.callback

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
import com.rejnek.oog.data.gameItems.GenericItemFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuestionFactory() : GenericItemFactory() {
    override val id: String = "question"
    override val js: String = """
        async function $id(questionText) {
        return await createCallback("$id", questionText);
        }
    """.trimIndent()

    override suspend fun create(data: String, callbackId: String) {
        val question = Question(
            questionText = data,
            onSubmit = { answer ->
                game?.resolveCallback(callbackId, answer)
            }
        )

        gameRepository?.addUIElement { question.Show() }
    }
}

class Question(
    questionText: String,
    onSubmit: (String) -> Unit
) {
    private val _questionText = MutableStateFlow(questionText)
    val questionText = _questionText.asStateFlow()

    private val _onSubmit = MutableStateFlow(onSubmit)
    val onSubmit = _onSubmit.asStateFlow()

    private val _answerText = MutableStateFlow("")
    val answerText = _answerText.asStateFlow()

    @Composable
    fun Show(
        modifier: Modifier = Modifier
    ) {
        val answerText by this.answerText.collectAsState()
        val onValueChange: (String) -> Unit = {
            _answerText.value = it
        }
        val text by questionText.collectAsState()
        val onSubmit by this.onSubmit.collectAsState()

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
            onClick = {
                if (answerText.isNotBlank()) {
                    onSubmit(answerText)
                }
            },
        ) {
            Text("Submit")
        }
    }
}