package com.rejnek.oog.engine.commands.direct.factory

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import com.rejnek.oog.engine.commands.GenericDirectFactory
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

// Note: this was heavily created by copilot the code is verbose but it works well enough for now
// Note: we are generating the share image in a Main thread so this can slow down the app when there is a lot of images to render
class ShareButtonFactory() : GenericDirectFactory() {
    override val id: String = "shareButton"

    override suspend fun create(data: String) {
        val repo = gameRepository ?: return
        val elementRef = @Composable {
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

    companion object {
        // Atomic flag used for the actual atomic compare-and-set when starting capture
        private val capturingAtomic = AtomicBoolean(false)
        // Observable state that composables can collect to show loading UI
        val capturingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    }

    private fun captureAndShare(
        context: Context,
        uiElements: List<@Composable () -> Unit>
    ) {
        if (!capturingAtomic.compareAndSet(false, true)) return
        capturingState.value = true
        val activity = context as? Activity ?: run { capturingAtomic.set(false); return }
        val root = activity.window?.decorView?.findViewById<ViewGroup>(android.R.id.content) ?: run { capturingAtomic.set(false); return }

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
                capturingAtomic.set(false)
                capturingState.value = false
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

        // Observe the factory's capturing state to show a loading indicator and disable the button
        val isLoading by ShareButtonFactory.capturingState.collectAsState()

        Button(
            onClick = onClick,
            enabled = !isLoading,
            modifier = modifier.fillMaxWidth().padding(8.dp),
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
            if(isLoading) Text(stringResource(R.string.share_result_are_generated))
            else Text(text)
        }
    }
}
