package com.example.expensetracker.ui.anim

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToLong

// Shared motion for every screen, so the whole app moves the same way.

// Delay between consecutive items in a staggered list
const val STAGGER_MILLIS = 45

/**
 * Fades and rises into place the first time this element appears.
 * Pass enabled = false for things that shouldn't animate (e.g. list rows scrolled back into view).
 */
fun Modifier.appear(
    delayMillis: Int = 0,
    offsetY: Dp = 24.dp,
    fromScale: Float = 1f,
    durationMillis: Int = 500,
    enabled: Boolean = true
): Modifier = if (!enabled) this else composed {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val progress by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis, delayMillis, FastOutSlowInEasing),
        label = "appear"
    )
    graphicsLayer {
        alpha = progress
        translationY = (1f - progress) * offsetY.toPx()
        val scale = fromScale + (1f - fromScale) * progress
        scaleX = scale
        scaleY = scale
    }
}

/**
 * True for the first moments a screen is showing. Lists use it so only the rows visible on arrival
 * animate in, and rows scrolled into view later just appear.
 */
@Composable
fun rememberIntroPlaying(durationMillis: Long = 900): Boolean {
    var playing by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(durationMillis)
        playing = false
    }
    return playing
}

/** Clickable that springs down slightly while pressed, with the normal ripple. */
fun Modifier.bounceClick(pressedScale: Float = 0.95f, onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "bounce"
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.clickable(interactionSource = interaction, indication = ripple(), onClick = onClick)
}

/** A rupee amount that counts up/down to its new value instead of jumping. */
@Composable
fun AnimatedRupees(
    value: Long,
    modifier: Modifier = Modifier,
    format: (Long) -> String = { "₹ $it" },
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
    durationMillis: Int = 800
) {
    val animated by animateFloatAsState(
        targetValue = value.toFloat(),
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
        label = "rupees"
    )
    // Float loses precision on big numbers, so show the exact value once the count has finished
    val shown = if (animated == value.toFloat()) value else animated.roundToLong()
    Text(
        text = format(shown),
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        style = style
    )
}
