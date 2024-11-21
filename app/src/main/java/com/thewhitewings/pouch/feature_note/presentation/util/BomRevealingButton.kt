package com.thewhitewings.pouch.feature_note.presentation.util

import android.content.res.ColorStateList
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.feature_note.util.Zone
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Button to reveal the Box of mysteries.
 */
@Composable
fun BomRevealingButton(
    onToggleZone: () -> Unit,
    focusManager: FocusManager,
    zone: Zone,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    var width by remember { mutableStateOf(0.dp) }
    width =
        if (zone == Zone.CREATIVE) 80.dp
        else 0.dp

    AndroidView(
        factory = { context ->
            // Create and configure the AppCompatButton
            AppCompatButton(context).apply {
                setBackgroundColor(Color.Transparent.toArgb())
                /*
                Background tint is needed after the fourth knock
                to make 'Revealed' word invisible when the button is not pressed.
                The word should only be visible when the button is pressed as a ripple effect.
                 */
                backgroundTintList = ColorStateList.valueOf(surfaceColor.toArgb())
                setOnClickListener {
                    focusManager.clearFocus()
                    knockBoxOfMysteries(this, onToggleZone)
                }
            }
        },
        update = { view ->
            // Any additional updates can go here if needed
        },
        modifier = modifier
            .testTag(stringResource(R.string.bom_button_tag))
            .padding(top = 2.dp)
            .size(width = width, height = 20.dp)
    )
}


// Count of how many times the Box of mysteries reveal button has been pressed (knocked)
private var bomKnocks = 0

// Boolean of whether the timeout for revealing the Box of mysteries has started
private var bomTimeoutStarted = false

/**
 * Knocks the door once and
 * Triggers the sequence of revealing the Box of mysteries if it has not been triggered.
 */
fun knockBoxOfMysteries(view: View, toggleZone: () -> Unit) {
    bomKnocks++
    if (!bomTimeoutStarted) {
        MainScope().launch {
            startBoxRevealTimeout(view, toggleZone)
        }
    }
}

/**
 * Starts the timeout for completing the sequence of revealing the Box of mysteries.
 * If the sequence of revealing the Box of mysteries is completed before the timeout,
 * the Box of mysteries will be revealed.
 * Otherwise, the time window will be closed and the sequence will be reset.
 *
 * The Bom should be knocked 5 times ([bomRevealingThreshold])
 * within 7 seconds ([timeoutKnocking]) to reveal the Box of mysteries.
 */
private suspend fun startBoxRevealTimeout(view: View, toggleZone: () -> Unit) {
    bomTimeoutStarted = true
    val timeoutKnocking = 7_000L // 7 seconds timeout
    val bomRevealingThreshold = 5 // 5 knocks to reveal the Box of mysteries
    val startKnockingTime = System.currentTimeMillis()

    while (bomTimeoutStarted) {
        val elapsedKnockingTime = System.currentTimeMillis() - startKnockingTime

        if (elapsedKnockingTime >= timeoutKnocking) {
            bomTimeoutStarted = false
            bomKnocks = 0
            break
        } else if (bomKnocks == 4) {
            view.setBackgroundResource(R.drawable.ripple_revealed_word)
        } else if (bomKnocks == bomRevealingThreshold) {
            delay(500) // wait 500ms before completing the reveal
            view.setBackgroundResource(0)
            bomTimeoutStarted = false
            bomKnocks = 0
            toggleZone()
            break
        }

        delay(200) // Wait for 200ms before checking again
    }
}