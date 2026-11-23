package com.example.expensetracker.Screens.Card

import com.example.expensetracker.ui.theme.DangerRed
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.expensetracker.Screens.common.CardFace
import com.example.expensetracker.Screens.common.ConfirmDialog
import com.example.expensetracker.Screens.common.available
import com.example.expensetracker.ViewModel.ViewModel
import com.example.expensetracker.dataclasses.Card
import com.example.expensetracker.ui.anim.appear
import com.example.expensetracker.ui.anim.bounceClick
import com.example.expensetracker.ui.rupees
import com.example.expensetracker.ui.theme.ExpenseColor
import com.example.expensetracker.ui.theme.IncomeColor
import com.example.expensetracker.ui.thisMonth

private val Danger = DangerRed

/**
 * Cards as a wallet: the selected card on top, the rest stacked below showing just their top edge.
 * Tap a stacked card to pull it up; tap the top card to flip it and see its numbers.
 */
@Composable
fun cardsScreen(viewModel: ViewModel, onAddCard: () -> Unit, onEditCard: () -> Unit) {
    val state by viewModel.cards
    var confirmDelete by remember { mutableStateOf<Card?>(null) }

    val screenState = when {
        state.error != null -> "error"
        state.loading -> "loading"
        state.cards.isEmpty() -> "empty"
        else -> "wallet"
    }

    Box(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Crossfade(targetState = screenState, label = "cardsState") { shown ->
            when (shown) {
                "error" -> Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.error.orEmpty(), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = { viewModel.getCards() }) { Text("Retry") }
                }
                "loading" -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
                "empty" -> EmptyWallet(onAddCard)
                else -> Wallet(
                    viewModel = viewModel,
                    cards = state.cards,
                    onEdit = {
                        viewModel.card = viewModel.selectedCard.copy()
                        onEditCard()
                    },
                    onDelete = { confirmDelete = viewModel.selectedCard }
                )
            }
        }

        if (screenState == "wallet") {
            AddButton(
                onClick = onAddCard,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .appear(delayMillis = 400, fromScale = 0.3f, offsetY = 0.dp)
            )
        }
    }

    confirmDelete?.let { card ->
        ConfirmDialog(
            title = "Delete card?",
            message = "The card ending ${card.cardNo.takeLast(4)} and its ${card.transaction.size} transactions will be deleted. This can't be undone.",
            confirmText = "Delete",
            onConfirm = { viewModel.deleteCard(card.cardId!!) },
            onDismiss = { confirmDelete = null }
        )
    }
}

@Composable
private fun Wallet(viewModel: ViewModel, cards: List<Card>, onEdit: () -> Unit, onDelete: () -> Unit) {
    val haptics = LocalHapticFeedback.current
    val selected = viewModel.selectedCardIndex.coerceIn(0, cards.lastIndex)
    var flipped by remember { mutableStateOf(false) }
    // A newly chosen card always arrives face up
    LaunchedEffect(selected) { flipped = false }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.appear()) {
            Text("Cards", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            // Actions for the top card, popping in fresh whenever it changes
            AnimatedContent(
                targetState = selected,
                transitionSpec = { (scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn()) togetherWith (scaleOut() + fadeOut()) },
                label = "actions"
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RoundIcon(Icons.Filled.Edit, "Edit card", MaterialTheme.colorScheme.onSurface, onEdit)
                    RoundIcon(Icons.Filled.DeleteOutline, "Delete card", Danger, onDelete)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val cardHeight = maxWidth / 1.586f
            val peek = 62.dp      // how much of each stacked card shows
            val gap = 64.dp       // space between the top card and the stack (holds the flip button)
            val others = cards.indices.filter { it != selected }
            val totalHeight = if (others.isEmpty()) cardHeight + gap
            else cardHeight + gap + peek * (others.size - 1) + cardHeight

            Box(Modifier.fillMaxWidth().height(totalHeight)) {
                cards.forEachIndexed { index, card ->
                    val isTop = index == selected
                    val rank = others.indexOf(index)
                    // Every card springs to its slot; the chosen one rises to the top, the old one sinks into the stack
                    val y by animateDpAsState(
                        targetValue = if (isTop) 0.dp else cardHeight + gap + peek * rank,
                        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessLow),
                        label = "cardY"
                    )
                    val scale by animateFloatAsState(if (isTop) 1f else 0.94f, spring(dampingRatio = 0.7f), label = "cardScale")

                    Box(
                        Modifier
                            .zIndex(if (isTop) 100f else rank.toFloat())
                            .offset(y = y)
                            // Cards fly up into the wallet one after another on arrival
                            .appear(delayMillis = 120 + index * 90, offsetY = 260.dp, durationMillis = 650)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                    ) {
                        if (isTop) {
                            FlippableCard(
                                card = card,
                                flipped = flipped,
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    flipped = !flipped
                                }
                            )
                        } else {
                            CardFace(
                                card,
                                Modifier
                                    .shadow(10.dp, RoundedCornerShape(22.dp))
                                    .bounceClick(pressedScale = 0.97f) {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.selectCard(index)
                                    }
                            )
                        }
                    }
                }

                // Flip button in the gap under the top card; its arrows turn as the card turns
                val spin by animateFloatAsState(if (flipped) 180f else 0f, tween(500), label = "spin")
                Box(
                    Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = cardHeight + 12.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .bounceClick {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            flipped = !flipped
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Sync,
                        contentDescription = if (flipped) "Show card front" else "Show card details",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.graphicsLayer { rotationZ = spin }
                    )
                }
            }
        }
    }
}

// Turns around its vertical axis in 3D; the back appears once it's past halfway
@Composable
private fun FlippableCard(card: Card, flipped: Boolean, onClick: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(550, easing = FastOutSlowInEasing),
        label = "flip"
    )
    Box(
        Modifier
            .shadow(16.dp, RoundedCornerShape(22.dp))
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .bounceClick(pressedScale = 0.98f, onClick = onClick)
    ) {
        if (rotation <= 90f) {
            CardFace(card)
        } else {
            // Mirror the back so it reads the right way round
            Box(Modifier.graphicsLayer { rotationY = 180f }) { CardBack(card) }
        }
    }
}

// The back of the card: the numbers, drawn rather than written
@Composable
private fun CardBack(card: Card) {
    val start = if (card.credit) Color(0xFFFF9A62) else Color(0xFF8B5CFF)
    val end = if (card.credit) Color(0xFFE5533D) else Color(0xFF3F1DB3)
    val shape = RoundedCornerShape(22.dp)

    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(1.586f)
            .clip(shape)
            .background(Brush.linearGradient(listOf(end, start)))
            .border(1.dp, Color.White.copy(alpha = 0.25f), shape)
    ) {
        // Magnetic stripe
        Box(
            Modifier
                .padding(top = 22.dp)
                .fillMaxWidth()
                .height(34.dp)
                .background(Color.Black.copy(alpha = 0.55f))
        )
        Row(
            Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 70.dp, bottom = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (card.credit) {
                val used = if (card.creditLimit > 0) (card.creditsUsed.toFloat() / card.creditLimit).coerceIn(0f, 1f) else 0f
                UsageRing(used, Modifier.fillMaxHeight().aspectRatio(1f))
                Spacer(Modifier.width(18.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BackStat(Icons.Filled.ArrowUpward, rupees(card.creditsUsed), "used")
                    BackStat(Icons.Filled.ArrowDownward, rupees(card.available()), "left")
                }
            } else {
                val month = card.thisMonth()
                Column(Modifier.weight(1f)) {
                    Text(rupees(card.balance), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        BackStat(Icons.Filled.ArrowDownward, rupees(month.income), "in")
                        BackStat(Icons.Filled.ArrowUpward, rupees(month.expense), "out")
                    }
                }
                FlowBars(month.income, month.expense, Modifier.fillMaxHeight().width(46.dp))
            }
        }
    }
}

// Credit used as a ring that sweeps up to its value, percentage in the middle
@Composable
private fun UsageRing(fraction: Float, modifier: Modifier = Modifier) {
    val sweep = remember { Animatable(0f) }
    LaunchedEffect(fraction) { sweep.animateTo(fraction, tween(1000, delayMillis = 250, easing = FastOutSlowInEasing)) }
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = size.minDimension * 0.12f
            val inset = stroke / 2
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(Color.White.copy(alpha = 0.25f), 0f, 360f, false, Offset(inset, inset), arcSize, style = Stroke(stroke))
            drawArc(
                if (fraction > 0.8f) Color(0xFFFFD1D1) else Color.White,
                -90f,
                360f * sweep.value,
                false,
                Offset(inset, inset),
                arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
        }
        Text("${(sweep.value * 100).toInt()}%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

// This month's in vs out as two bars that grow when the back is shown
@Composable
private fun FlowBars(income: Long, expense: Long, modifier: Modifier = Modifier) {
    val max = maxOf(income, expense, 1L).toFloat()
    val grow = remember { Animatable(0f) }
    LaunchedEffect(Unit) { grow.animateTo(1f, tween(900, delayMillis = 250, easing = FastOutSlowInEasing)) }
    Canvas(modifier) {
        val barWidth = size.width * 0.36f
        val radius = CornerRadius(barWidth / 2)
        listOf(income to Color.White, expense to Color.White.copy(alpha = 0.55f)).forEachIndexed { i, (value, color) ->
            val height = (value / max) * size.height * grow.value
            drawRoundRect(
                color = Color.White.copy(alpha = 0.15f),
                topLeft = Offset(i * (size.width - barWidth), 0f),
                size = Size(barWidth, size.height),
                cornerRadius = radius
            )
            drawRoundRect(
                color = color,
                topLeft = Offset(i * (size.width - barWidth), size.height - height),
                size = Size(barWidth, height),
                cornerRadius = radius
            )
        }
    }
}

@Composable
private fun BackStat(icon: ImageVector, value: String, caption: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = caption, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text(value, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
private fun RoundIcon(icon: ImageVector, description: String, tint: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.10f))
            .bounceClick(pressedScale = 0.85f, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = description, tint = tint)
    }
}

// Round gradient "+" that breathes gently so it's easy to spot
@Composable
private fun AddButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseScale"
    )
    Box(
        modifier
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            }
            .size(60.dp)
            .shadow(14.dp, CircleShape, spotColor = IncomeColor)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(Color(0xFF8B5CFF), IncomeColor)))
            .bounceClick(pressedScale = 0.85f, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add card", tint = Color.White, modifier = Modifier.size(28.dp))
    }
}

// No cards: a dashed card outline with a breathing "+"; tap anywhere on it to add one
@Composable
private fun EmptyWallet(onAddCard: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "empty")
    val pulse by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "emptyPulse"
    )
    val dash by transition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(tween(1600)),
        label = "dash"
    )
    val outline = MaterialTheme.colorScheme.outline

    Column(
        Modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1.586f)
                .appear(fromScale = 0.9f)
                .bounceClick(pressedScale = 0.96f, onClick = onAddCard),
            contentAlignment = Alignment.Center
        ) {
            // Dashed outline whose dashes slowly travel around the card
            Canvas(Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = outline,
                    cornerRadius = CornerRadius(22.dp.toPx()),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), phase = -dash)
                    )
                )
            }
            Box(
                Modifier
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    }
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF8B5CFF), IncomeColor))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add card", tint = Color.White, modifier = Modifier.size(30.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Add your first card", fontWeight = FontWeight.SemiBold, modifier = Modifier.appear(delayMillis = 150))
    }
}
