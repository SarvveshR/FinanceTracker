package com.example.expensetracker.Screens.LoginScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.auth.AuthViewModel
import com.example.expensetracker.ui.theme.ExpenseColor
import com.example.expensetracker.ui.theme.IncomeColor

private val BackgroundTop = Color(0xFF1E1240)
private val BackgroundBottom = Color(0xFF0B0820)
private val IncomeGreen = Color(0xFF2ED47A)

@Composable
fun LoginScreen(authViewModel: AuthViewModel) {
    val context = LocalContext.current

    // Flips once on first frame; every element animates in from it with its own delay
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundTop, BackgroundBottom)))
    ) {
        GlowBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            FloatingCards(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .enterAnimation(started, delayMillis = 0, fromScale = 0.85f)
            )

            Spacer(Modifier.height(36.dp))

            Text(
                text = "Your money,\nbeautifully organised.",
                color = Color.White,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.enterAnimation(started, delayMillis = 200)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Track every card, income and expense — and see where it all goes.",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 15.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .enterAnimation(started, delayMillis = 350)
            )

            Spacer(Modifier.weight(1f))

            AnimatedVisibility(
                visible = authViewModel.errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ErrorPill(authViewModel.errorMessage.orEmpty())
            }

            GoogleButton(
                isLoading = authViewModel.isSigningIn,
                onClick = { authViewModel.signIn(context) },
                modifier = Modifier.enterAnimation(started, delayMillis = 500)
            )

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.enterAnimation(started, delayMillis = 650)
            ) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.45f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Secure sign-in with your Google account",
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// Fade + rise (and optional grow) the first time the screen appears
private fun Modifier.enterAnimation(started: Boolean, delayMillis: Int, fromScale: Float = 1f): Modifier = composed {
    val progress by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 800, delayMillis = delayMillis, easing = FastOutSlowInEasing),
        label = "enter"
    )
    graphicsLayer {
        alpha = progress
        translationY = (1f - progress) * 40.dp.toPx()
        val scale = fromScale + (1f - fromScale) * progress
        scaleX = scale
        scaleY = scale
    }
}

// Two soft glows that slowly drift behind everything
@Composable
private fun GlowBackground() {
    val transition = rememberInfiniteTransition(label = "glow")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "drift"
    )

    Canvas(Modifier.fillMaxSize()) {
        val purpleCenter = Offset(size.width * (0.15f + 0.25f * drift), size.height * (0.22f + 0.08f * drift))
        drawCircle(
            brush = Brush.radialGradient(
                listOf(IncomeColor.copy(alpha = 0.55f), Color.Transparent),
                center = purpleCenter,
                radius = size.width * 0.75f
            ),
            radius = size.width * 0.75f,
            center = purpleCenter
        )
        val orangeCenter = Offset(size.width * (0.95f - 0.25f * drift), size.height * (0.55f - 0.1f * drift))
        drawCircle(
            brush = Brush.radialGradient(
                listOf(ExpenseColor.copy(alpha = 0.35f), Color.Transparent),
                center = orangeCenter,
                radius = size.width * 0.6f
            ),
            radius = size.width * 0.6f,
            center = orangeCenter
        )
    }
}

// Hero: two tilted cards floating out of sync, with an income and an expense chip bobbing around them
@Composable
private fun FloatingCards(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "float")
    val bob by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bob"
    )
    val chipBob by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "chipBob"
    )
    // A light sweep that crosses the front card every few seconds
    val shine by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(4000, delayMillis = 1200, easing = LinearEasing)),
        label = "shine"
    )

    Box(modifier, contentAlignment = Alignment.Center) {
        HeroCard(
            brush = Brush.linearGradient(listOf(Color(0xFFFF9A62), ExpenseColor, Color(0xFFE5533D))),
            label = "CREDIT",
            lastDigits = "8842",
            shine = null,
            modifier = Modifier
                .offset(x = 28.dp, y = (-46).dp + (bob * 6).dp)
                .graphicsLayer { rotationZ = 9f }
        )
        HeroCard(
            brush = Brush.linearGradient(listOf(Color(0xFF8B5CFF), IncomeColor, Color(0xFF3F1DB3))),
            label = "DEBIT",
            lastDigits = "2049",
            shine = shine,
            modifier = Modifier
                .offset(x = (-18).dp, y = 34.dp - (bob * 9).dp)
                .graphicsLayer { rotationZ = -7f }
        )

        AmountChip(
            icon = Icons.Filled.ArrowDownward,
            iconColor = IncomeGreen,
            title = "Salary",
            amount = "+ ₹48,200",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 0.dp, y = 8.dp + (chipBob * 5).dp)
        )
        AmountChip(
            icon = Icons.Filled.ArrowUpward,
            iconColor = ExpenseColor,
            title = "Food & Groceries",
            amount = "− ₹1,250",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 0.dp, y = (-6).dp - (chipBob * 5).dp)
        )
    }
}

@Composable
private fun HeroCard(
    brush: Brush,
    label: String,
    lastDigits: String,
    shine: Float?,
    modifier: Modifier = Modifier,
    width: Dp = 250.dp,
    height: Dp = 155.dp
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .size(width, height)
            .clip(shape)
            .background(brush)
            .border(1.dp, Color.White.copy(alpha = 0.25f), shape)
    ) {
        // Decorative rings in the corner
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(Color.White.copy(alpha = 0.08f), radius = size.height * 0.7f, center = Offset(size.width * 1.02f, -size.height * 0.05f))
            drawCircle(Color.White.copy(alpha = 0.06f), radius = size.height * 0.45f, center = Offset(size.width * 0.9f, size.height * 1.05f))
            if (shine != null) {
                val x = size.width * shine
                drawRect(
                    brush = Brush.linearGradient(
                        listOf(Color.Transparent, Color.White.copy(alpha = 0.22f), Color.Transparent),
                        start = Offset(x - size.width * 0.3f, 0f),
                        end = Offset(x + size.width * 0.3f, size.height)
                    )
                )
            }
        }

        Column(Modifier.fillMaxSize().padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Card chip
                Box(
                    Modifier
                        .size(34.dp, 25.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFFFFE29A), Color(0xFFE0B04F))))
                )
                Spacer(Modifier.weight(1f))
                Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            }
            Spacer(Modifier.weight(1f))
            Text(
                "••••  ••••  ••••  $lastDigits",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("EXPENSE TRACKER", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, letterSpacing = 1.5.sp)
                Spacer(Modifier.weight(1f))
                // Two overlapping circles, network-logo style
                Box(Modifier.size(20.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.55f)))
                Box(Modifier.offset(x = (-8).dp).size(20.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.3f)))
            }
        }
    }
}

@Composable
private fun AmountChip(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            Text(amount, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ErrorPill(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFF5A5A).copy(alpha = 0.15f))
            .border(1.dp, Color(0xFFFF5A5A).copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = Color(0xFFFF8A8A), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(message, color = Color(0xFFFFC2C2), fontSize = 13.sp, lineHeight = 18.sp)
    }
}

// White pill button with the Google "G"; shrinks a little while pressed and shows a spinner while signing in
@Composable
private fun GoogleButton(isLoading: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.96f else 1f, label = "press")

    Button(
        onClick = onClick,
        enabled = !isLoading,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF1F1F1F),
            disabledContainerColor = Color.White.copy(alpha = 0.85f),
            disabledContentColor = Color(0xFF1F1F1F)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .scale(scale)
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
            label = "buttonContent"
        ) { loading ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                if (loading) {
                    CircularProgressIndicator(color = IncomeColor, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Signing in…", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                } else {
                    GoogleLogo(Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Continue with Google", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// The four-colour "G", drawn so there's no image asset to add
@Composable
private fun GoogleLogo(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = size.width * 0.2f
        val inset = stroke / 2
        val arcSize = Size(size.width - stroke, size.height - stroke)
        val topLeft = Offset(inset, inset)
        val style = Stroke(width = stroke, cap = StrokeCap.Butt)
        // Angles: 0° = 3 o'clock, clockwise; the gap at the top-right is the G's opening
        drawArc(Color(0xFFEA4335), startAngle = 200f, sweepAngle = 115f, useCenter = false, topLeft = topLeft, size = arcSize, style = style)
        drawArc(Color(0xFFFBBC05), startAngle = 135f, sweepAngle = 65f, useCenter = false, topLeft = topLeft, size = arcSize, style = style)
        drawArc(Color(0xFF34A853), startAngle = 45f, sweepAngle = 90f, useCenter = false, topLeft = topLeft, size = arcSize, style = style)
        drawArc(Color(0xFF4285F4), startAngle = 0f, sweepAngle = 45f, useCenter = false, topLeft = topLeft, size = arcSize, style = style)
        // The G's crossbar
        drawRect(
            Color(0xFF4285F4),
            topLeft = Offset(size.width / 2, size.height / 2 - stroke / 2),
            size = Size(size.width / 2 - inset + stroke / 2, stroke)
        )
    }
}
