package com.rejnek.oog.data.gameItems.direct.commands

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import com.rejnek.oog.data.gameItems.GenericDirectFactory
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.FileProvider
import androidx.core.view.doOnPreDraw
import com.rejnek.oog.ui.theme.AppTheme
import android.view.View
import androidx.compose.foundation.background
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean
import androidx.core.graphics.createBitmap

class ShareButtonFactory() : GenericDirectFactory() {
    override val id: String = "shareButton"

    override suspend fun create(data: String) {
        val repo = gameRepository ?: return
        repo.addUIElement {
            val context = LocalContext.current
            ShareButton({ captureAndShare(context, repo.gameUIRepository.uiElements.value) }).Show()
        }
    }

    private val capturing = AtomicBoolean(false)

    private fun captureAndShare(
        context: Context,
        uiElements: List<@Composable () -> Unit>
    ) {
        if (!capturing.compareAndSet(false, true)) return
        val activity = context as? Activity ?: run { capturing.set(false); return }
        val root = activity.window?.decorView?.findViewById<ViewGroup>(android.R.id.content) ?: run { capturing.set(false); return }

        val composeView = ComposeView(context).apply {
            // Invisible but participates in layout
            visibility = View.INVISIBLE
            setContent {
                AppTheme {
                    val bg = MaterialTheme.colorScheme.background
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bg),
                        color = bg
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            uiElements.forEachIndexed { index, element ->
                                element()
                                if (index != uiElements.lastIndex) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        root.addView(
            composeView,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )

        composeView.doOnPreDraw {
            composeView.post {
                val w = composeView.width
                val h = composeView.height
                if (w > 0 && h > 0) {
                    val safeHeight = h.coerceAtMost(16000)
                    val bitmap = createBitmap(w, safeHeight)
                    val canvas = Canvas(bitmap)
                    composeView.draw(canvas)
                    shareBitmap(context, bitmap)
                }
                root.removeView(composeView)
                capturing.set(false)
            }
        }
    }

    private fun shareBitmap(context: Context, bitmap: Bitmap) {
        val cacheDir = File(context.cacheDir, "shares").apply { mkdirs() }
        val file = File(cacheDir, "game_share_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share results"))
    }
}

class ShareButton(
    onClick: () -> Unit
) {
    private val _onButtonClick = MutableStateFlow<() -> Unit>(onClick)
    val onButtonClick = _onButtonClick.asStateFlow()

    @Composable
    fun Show(
        modifier: Modifier = Modifier
    ) {
        val text = "Share the results!"
        val onClick by onButtonClick.collectAsState()

        val randomColorIndex = remember { Random.nextInt(3) }
        val selectedColor = when (randomColorIndex) {
            0 -> MaterialTheme.colorScheme.primary
            1 -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.tertiary
        }

        Button(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = selectedColor,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share Icon",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = text)
        }
    }
}
