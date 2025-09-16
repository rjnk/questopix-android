package com.rejnek.oog.engine.commands.direct.factory

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rejnek.oog.R
import com.rejnek.oog.engine.commands.GenericDirectFactory

class PopUpFactory : GenericDirectFactory() {
    override val id = "popUp"

    override suspend fun createWithArgs(args: List<String>) {
        Log.d("PopUpFactory", "createWithArgs: $args")

        val text = args.getOrNull(0) ?: return
        val task = args.getOrNull(1)

        if(task == null) textPopUP(text)
        else taskPopUP(text, task)
    }

    fun taskPopUP(text: String, task: String) {
        gameRepository?.currentGamePackage?.value?.currentTaskId = task
        gameRepository?.evaluateJs("save();")

        val popup = PopUp(text = text) {
            gameRepository?.evaluateJs("showTask(\"$task\")")
        }

        gameRepository?.addUIElement {
            popup.Show()
        }
    }

    fun textPopUP(text: String) {
        val popup = PopUp(
            text = text,
            onContinue = { gameRepository?.removeLastUIElement() }
        )
        gameRepository?.addUIElement {
            popup.Show()
        }
    }
}

class PopUp(
    private val text: String,
    private val onContinue: () -> Unit = {},
) {
    @Composable
    fun Show() {
        var isVisible by remember { mutableStateOf(true) }

        if(!isVisible) return
        Dialog(
            onDismissRequest = { /* Don't allow dismiss by clicking outside */ },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = text,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            onContinue()
                            isVisible = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.continue_label),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
