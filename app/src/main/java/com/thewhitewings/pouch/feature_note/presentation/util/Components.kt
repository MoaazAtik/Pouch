package com.thewhitewings.pouch.feature_note.presentation.util

import android.content.Context
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thewhitewings.pouch.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Draws a shape with a custom shadow.
 */
fun Modifier.customShadowedShape(
    color: Color,
    shadowColor: Color = DefaultShadowColor,
    blurRadius: Dp = 12.dp, // shadow elevation
    cornerRadius: Dp = 8.dp
) = this
    // Shadow
    .graphicsLayer {
        shadowElevation = blurRadius.toPx()
        spotShadowColor = shadowColor
        ambientShadowColor = shadowColor
        shape = RoundedCornerShape(cornerRadius)
        clip = true
    }
    .drawBehind {
        // Shape
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                this.color = color
            }
            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = 0f,
                        top = 0f,
                        right = size.width,
                        bottom = size.height,
                        radiusX = cornerRadius.toPx(),
                        radiusY = cornerRadius.toPx()
                    )
                )
            }
            canvas.drawPath(path, paint)
        }
    }

/**
 * Shows a snackbar with a message and an undo action to restore the recently deleted note.
 */
fun showRestoreNoteSnackbar(
    context: Context,
    snackbarHostState: SnackbarHostState,
    onNoteRestore: () -> Unit
) {
    MainScope().launch {
        val snackbarResult = snackbarHostState.showSnackbar(
            message = context.getString(R.string.note_deletion_snackbar_message),
            actionLabel = context.getString(R.string.note_deletion_snackbar_action_undo),
            duration = SnackbarDuration.Long
        )
        if (snackbarResult == SnackbarResult.ActionPerformed)
            onNoteRestore()
    }
}