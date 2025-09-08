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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rejnek.oog.R
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
import android.view.View.MeasureSpec
import com.rejnek.oog.data.repository.UiCaptureExclusions
import com.rejnek.oog.data.repository.LocalCaptureMode
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.graphics.createBitmap

// Note: this was heavily created by copilot
class ShareButtonFactory() : GenericDirectFactory() {
    override val id: String = "shareButton"

    override suspend fun create(data: String) {
        val repo = gameRepository ?: return

        var elementRef: (@Composable () -> Unit)? = null
        elementRef = {
            val context = LocalContext.current
            ShareButton {
                val all = repo.gameUIRepository.uiElements.value
                captureAndShare(context, all)
            }.Show()
        }

        // Register for exclusion
        UiCaptureExclusions.excluded.add(elementRef)
        repo.addUIElement(elementRef)
    }

    private val capturing = AtomicBoolean(false)

    private fun captureAndShare(
        context: Context,
        uiElements: List<@Composable () -> Unit>
    ) {
        if (!capturing.compareAndSet(false, true)) return
        val activity = context as? Activity ?: run { capturing.set(false); return }
        val root = activity.window?.decorView?.findViewById<ViewGroup>(android.R.id.content) ?: run { capturing.set(false); return }

        // Filter out any composables registered in the exclusion set
        val filtered = uiElements.filter { candidate -> UiCaptureExclusions.excluded.none { it === candidate } }

        val composeView = ComposeView(context).apply {
            visibility = View.INVISIBLE
            setContent {
                CompositionLocalProvider(LocalCaptureMode provides true) { // enable capture mode
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
                                filtered.forEachIndexed { index, element ->
                                    element()
                                    if (index != filtered.lastIndex) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
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
                val targetWidth = root.width.takeIf { it > 0 } ?: composeView.width
                if (targetWidth > 0) {
                    val wSpec = MeasureSpec.makeMeasureSpec(targetWidth, MeasureSpec.EXACTLY)
                    val hSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    composeView.measure(wSpec, hSpec)
                    composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)
                }
                val w = composeView.measuredWidth
                val h = composeView.measuredHeight
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
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_results)))
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
        val text = stringResource(R.string.share_results)
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
                contentDescription = stringResource(R.string.cd_share_icon),
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = text)
        }
    }
}
