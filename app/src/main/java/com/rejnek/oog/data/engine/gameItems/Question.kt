package com.rejnek.oog.data.engine.gameItems

import android.util.Log
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
        Log.d("Question", "Running with data: $data and callbackId: $callbackId")

        _questionText.value = data
        _provideAnswer.value = { answer ->
            Log.d("Question", "Answer provided: $answer")
            game?.resolveCallback(callbackId, answer)
        }

        _visible.value = true
    }

    override fun clear() {
        _visible.value = false
    }

    val _questionText = MutableStateFlow("Err")
    val questionText = _questionText.asStateFlow()

    val _provideAnswer = MutableStateFlow<(String) -> Unit>({})
    val provideAnswer = _provideAnswer.asStateFlow()

    private val _answerText = MutableStateFlow("")
    val answerText = _answerText.asStateFlow()

    private val _visible = MutableStateFlow(false)
    val isVisible: StateFlow<Boolean> = _visible.asStateFlow()


    @Composable
    fun Show(
        modifier: Modifier = Modifier
    ) {
        val answerText by this.answerText.collectAsState()
        val onValueChange: (String) -> Unit = {
            _answerText.value = it
        }
        val text = questionText.collectAsState().value
        val onSubmit: () -> Unit = {
            if (answerText.isNotBlank()) {
                provideAnswer.value(answerText)
            }
        }

        if( !isVisible.collectAsState().value) {
            return
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