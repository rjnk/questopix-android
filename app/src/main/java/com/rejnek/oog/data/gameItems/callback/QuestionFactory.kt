package com.rejnek.oog.data.gameItems.callback

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rejnek.oog.data.gameItems.GenericCallbackFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuestionFactory() : GenericCallbackFactory() {
    override val id: String = "question"

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

        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(16.dp)
            )

            OutlinedTextField(
                value = answerText,
                onValueChange = onValueChange,
                label = { Text("Your answer") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Button(
                onClick = { onSubmit(answerText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Submit")
            }
        }
    }
}