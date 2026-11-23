package com.example.expensetracker.Screens.SettingScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.Screens.common.ConfirmDialog
import com.example.expensetracker.auth.AuthUser
import com.example.expensetracker.ui.anim.appear
import com.example.expensetracker.ui.anim.bounceClick
import com.example.expensetracker.ui.theme.DangerRed
import com.example.expensetracker.ui.theme.DarkBackground
import com.example.expensetracker.ui.theme.DarkSurfaceHigh
import com.example.expensetracker.ui.theme.ExpenseColor
import com.example.expensetracker.ui.theme.IncomeColor
import com.example.expensetracker.ui.theme.LightBackground
import com.example.expensetracker.ui.theme.LightSurfaceHigh
import com.example.expensetracker.ui.theme.ThemeMode

@Composable
fun Settings(
    user: AuthUser,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onLogout: () -> Unit
) {
    var confirmLogout by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.appear())
        Spacer(Modifier.height(20.dp))

        ProfileCard(user, Modifier.appear(delayMillis = 80))

        SectionLabel("Appearance", Modifier.appear(delayMillis = 140))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.appear(delayMillis = 180)
        ) {
            ThemeOption(ThemeMode.SYSTEM, "System", Icons.Filled.BrightnessAuto, themeMode, onThemeChange, Modifier.weight(1f))
            ThemeOption(ThemeMode.LIGHT, "Light", Icons.Filled.LightMode, themeMode, onThemeChange, Modifier.weight(1f))
            ThemeOption(ThemeMode.DARK, "Dark", Icons.Filled.DarkMode, themeMode, onThemeChange, Modifier.weight(1f))
        }

        SectionLabel("Account", Modifier.appear(delayMillis = 220))
        Row(
            modifier = Modifier
                .appear(delayMillis = 260)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(DangerRed.copy(alpha = 0.10f))
                .bounceClick(pressedScale = 0.97f) { confirmLogout = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = DangerRed)
            Spacer(Modifier.width(12.dp))
            Text("Log out", color = DangerRed, fontWeight = FontWeight.SemiBold)
        }
    }

    if (confirmLogout) {
        ConfirmDialog(
            title = "Log out?",
            message = "You'll need to sign in with Google again to see your cards.",
            confirmText = "Log out",
            onConfirm = onLogout,
            onDismiss = { confirmLogout = false }
        )
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text.uppercase(),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
        modifier = modifier.padding(start = 4.dp, top = 28.dp, bottom = 10.dp)
    )
}

@Composable
private fun ProfileCard(user: AuthUser, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF8B5CFF), IncomeColor))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                user.name.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1) }.uppercase().ifBlank { "?" },
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(user.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                user.email,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// A tappable mini preview of the app in that mode. The chosen one gets a ring, a tick and a little spin of its icon.
@Composable
private fun ThemeOption(
    mode: ThemeMode,
    label: String,
    icon: ImageVector,
    current: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val selected = mode == current
    val ring by animateColorAsState(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent, label = "ring")
    val spin by animateFloatAsState(
        if (selected) 360f else 0f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "spin"
    )
    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .border(2.dp, ring, shape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .bounceClick(pressedScale = 0.94f) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSelect(mode)
            }
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            ThemePreview(mode)
            TickBadge(visible = selected, modifier = Modifier.align(Alignment.TopEnd))
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer { rotationZ = spin }
            )
            Spacer(Modifier.width(4.dp))
            Text(
                label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

// Tick that pops in on the chosen option
@Composable
private fun TickBadge(visible: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
        exit = scaleOut(tween(150)) + fadeOut(),
        modifier = modifier
    ) {
        Box(
            Modifier
                .padding(4.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
    }
}

// Tiny drawing of a screen with a card on it, in the colours that mode would use.
// "System" is split diagonally: half light, half dark.
@Composable
private fun ThemePreview(mode: ThemeMode) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .height(84.dp)
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
    ) {
        when (mode) {
            ThemeMode.LIGHT -> MiniScreen(LightBackground, LightSurfaceHigh)
            ThemeMode.DARK -> MiniScreen(DarkBackground, DarkSurfaceHigh)
            ThemeMode.SYSTEM -> {
                MiniScreen(LightBackground, LightSurfaceHigh)
                Box(
                    Modifier
                        .fillMaxSize()
                        .clip(DiagonalHalf)
                ) { MiniScreen(DarkBackground, DarkSurfaceHigh) }
            }
        }
    }
}

@Composable
private fun MiniScreen(background: Color, row: Color) {
    Column(
        Modifier
            .fillMaxSize()
            .background(background)
            .padding(8.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(30.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF8B5CFF), IncomeColor)))
        )
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth(0.8f).height(7.dp).clip(CircleShape).background(row))
        Spacer(Modifier.height(4.dp))
        Row {
            Box(Modifier.width(18.dp).height(7.dp).clip(CircleShape).background(ExpenseColor.copy(alpha = 0.7f)))
            Spacer(Modifier.width(4.dp))
            Box(Modifier.fillMaxWidth(0.6f).height(7.dp).clip(CircleShape).background(row))
        }
    }
}

// Bottom-right triangle, used to show half of the "System" preview in dark colours
private val DiagonalHalf = GenericShape { size, _ ->
    moveTo(size.width, 0f)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}
