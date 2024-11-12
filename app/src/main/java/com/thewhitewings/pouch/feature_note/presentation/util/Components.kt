package com.thewhitewings.pouch.feature_note.presentation.util

import androidx.compose.foundation.shape.RoundedCornerShape
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