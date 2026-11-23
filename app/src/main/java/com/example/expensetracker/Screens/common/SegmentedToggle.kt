package com.example.expensetracker.Screens.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Two-option pill with a coloured highlight that slides (with a little bounce) to the chosen side.
// Used for Expense | Income and Debit | Credit.
@Composable
fun SegmentedToggle(
    first: String,
    second: String,
    secondSelected: Boolean,
    accent: Color,
    onChange: (secondSelected: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 240.dp
) {
    BoxWithConstraints(
        modifier = modifier
            .width(width)
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        val half = maxWidth / 2
        val indicatorOffset by animateDpAsState(
            targetValue = if (secondSelected) half else 0.dp,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
            label = "toggle"
        )
        Box(
            Modifier
                .offset(x = indicatorOffset)
                .width(half)
                .fillMaxHeight()
                .clip(RoundedCornerShape(18.dp))
                .background(accent)
        )
        Row(Modifier.fillMaxSize()) {
            listOf(false to first, true to second).forEach { (value, label) ->
                val selected = value == secondSelected
                val textColor by animateColorAsState(
                    if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "toggleText"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onChange(value) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = textColor, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
