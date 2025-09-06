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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rejnek.oog.data.gameItems.GenericDirectFactory

class PopUpFactory : GenericDirectFactory() {
    override val id = "popUp"

    override suspend fun createWithArgs(args: List<String>) {
        val text = args.getOrNull(0) ?: throw IllegalStateException("PopUp requires a text argument")
        val task = args.getOrNull(1) ?: throw IllegalStateException("PopUp requires a task argument")

        gameRepository?.currentGamePackage?.value?.currentTaskId = task
        gameRepository?.evaluateJs("save();")

        val popup = PopUp(text = text) {
            gameRepository?.evaluateJs("showTask(\"$task\")")
        }

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
                        onClick = onContinue,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "Continue",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
