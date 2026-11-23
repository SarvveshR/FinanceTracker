package com.example.expensetracker.Screens.HomeScreen

import com.example.expensetracker.ui.theme.PositiveGreen
import com.example.expensetracker.ui.theme.DangerRed
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.example.expensetracker.R
import com.example.expensetracker.Screens.common.CardFace
import com.example.expensetracker.Screens.common.ConfirmDialog
import com.example.expensetracker.ViewModel.ViewModel
import com.example.expensetracker.auth.AuthUser
import com.example.expensetracker.dataclasses.Card
import com.example.expensetracker.dataclasses.Transaction
import com.example.expensetracker.ui.anim.AnimatedRupees
import com.example.expensetracker.ui.anim.STAGGER_MILLIS
import com.example.expensetracker.ui.anim.appear
import com.example.expensetracker.ui.anim.bounceClick
import com.example.expensetracker.ui.anim.rememberIntroPlaying
import com.example.expensetracker.ui.friendlyDay
import com.example.expensetracker.ui.localDate
import com.example.expensetracker.ui.localDateTime
import com.example.expensetracker.ui.rupees
import com.example.expensetracker.ui.thisMonth
import com.example.expensetracker.ui.theme.ExpenseColor
import com.example.expensetracker.ui.theme.IncomeColor
import java.time.LocalTime
import kotlin.math.absoluteValue

/**
 * Dashboard: greeting, swipeable card carousel (swiping picks the card), this month's in/out,
 * and the card's transactions grouped by day. Tap a transaction to edit, swipe left to delete.
 */
@Composable
fun homeScreen(
    viewModel: ViewModel,
    user: AuthUser,
    goToCardScreen: () -> Unit,
    goToAddCardScreen: () -> Unit,
    goToEditTransaction: () -> Unit
) {
    val state by viewModel.cards
    var pendingDelete by remember { mutableStateOf<Transaction?>(null) }
    val introPlaying = rememberIntroPlaying()

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Greeting(user, Modifier.appear())

        val screenState = when {
            state.error != null -> "error"
            state.loading -> "loading"
            state.cards.isEmpty() -> "empty"
            else -> "content"
        }
        Crossfade(targetState = screenState, label = "homeState") { shown ->
            when (shown) {
                "error" -> CenteredMessage(state.error.orEmpty(), "Retry") { viewModel.getCards() }
                "loading" -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
                "empty" -> CenteredMessage("No cards yet. Add a card to start tracking.", "Add card", goToAddCardScreen)
                else -> {
                    val card = viewModel.selectedCard
                    // Newest first, grouped by day; keep each item's real index for edit
                    val groups = card.transaction.withIndex()
                        .sortedByDescending { it.value.localDateTime() }
                        .groupBy { it.value.localDate() }
                        .toList()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 96.dp) // clear of the + button
                    ) {
                        item(key = "cards") {
                            CardCarousel(
                                viewModel = viewModel,
                                cards = state.cards,
                                onCardClick = goToCardScreen,
                                onAddCard = goToAddCardScreen,
                                modifier = Modifier.appear(delayMillis = 80, fromScale = 0.94f)
                            )
                        }
                        item(key = "month") {
                            MonthSummary(card, Modifier.appear(delayMillis = 160))
                        }
                        item(key = "title") {
                            Text(
                                "Transactions",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(start = 20.dp, top = 24.dp, bottom = 4.dp)
                                    .appear(delayMillis = 220)
                            )
                        }
                        if (groups.isEmpty()) {
                            item(key = "none") {
                                Text(
                                    "No transactions on this card yet.\nTap + to add one.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp)
                                        .appear(delayMillis = 260)
                                )
                            }
                        }
                        // Stagger positions are worked out here while building, not inside the items
                        var position = 0
                        groups.forEach { (day, rows) ->
                            val headerPosition = position
                            val rowsStart = position + 1
                            position += rows.size + 1
                            item(key = "day-$day") {
                                Text(
                                    friendlyDay(day),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .animateItem()
                                        .padding(start = 20.dp, top = 14.dp, bottom = 6.dp)
                                        .appear(
                                            delayMillis = 260 + headerPosition * STAGGER_MILLIS,
                                            enabled = introPlaying && headerPosition < 12
                                        )
                                )
                            }
                            itemsIndexed(rows, key = { _, row -> row.value.id ?: "i${row.index}" }) { i, (index, transaction) ->
                                val rowPosition = rowsStart + i
                                TransactionRow(
                                    viewModel = viewModel,
                                    transaction = transaction,
                                    onClick = {
                                        viewModel.startEditTransaction(index)
                                        goToEditTransaction()
                                    },
                                    onSwipeDelete = { pendingDelete = transaction },
                                    modifier = Modifier
                                        .animateItem()
                                        .appear(
                                            delayMillis = 260 + rowPosition * STAGGER_MILLIS,
                                            enabled = introPlaying && rowPosition < 12
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { transaction ->
        ConfirmDialog(
            title = "Delete transaction?",
            message = "${transaction.type.name} of ${rupees(transaction.amount.toLong())} will be removed and the card balance updated.",
            confirmText = "Delete",
            onConfirm = { viewModel.deleteTransaction(viewModel.selectedCard.cardId!!, transaction.id!!) },
            onDismiss = { pendingDelete = null }
        )
    }
}

@Composable
private fun Greeting(user: AuthUser, modifier: Modifier = Modifier) {
    val hour = LocalTime.now().hour
    val greeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
    val firstName = user.name.substringBefore(' ').ifBlank { "there" }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF8B5CFF), IncomeColor))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                user.name.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1) }.uppercase().ifBlank { "?" },
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(greeting, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Text(firstName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// Swipe between cards; the one in the middle is the selected card. Last page adds a card.
@Composable
private fun CardCarousel(
    viewModel: ViewModel,
    cards: List<Card>,
    onCardClick: () -> Unit,
    onAddCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(initialPage = viewModel.selectedCardIndex.coerceAtLeast(0)) { cards.size + 1 }

    // Swiping to a card selects it…
    LaunchedEffect(pagerState, cards.size) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            if (page < cards.size && page != viewModel.selectedCardIndex) viewModel.selectCard(page)
        }
    }
    // …and a card selected elsewhere (Cards tab) scrolls into view
    LaunchedEffect(viewModel.selectedCardIndex) {
        val index = viewModel.selectedCardIndex
        if (index >= 0 && index != pagerState.settledPage) pagerState.animateScrollToPage(index)
    }

    Column(modifier) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 28.dp),
            pageSpacing = 12.dp
        ) { page ->
            // Side cards shrink and fade a little
            val offset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue.coerceIn(0f, 1f)
            val pageModifier = Modifier.graphicsLayer {
                val scale = lerp(1f, 0.9f, offset)
                scaleX = scale
                scaleY = scale
                alpha = lerp(1f, 0.6f, offset)
            }
            if (page < cards.size) {
                CardFace(cards[page], pageModifier.bounceClick(pressedScale = 0.97f, onClick = onCardClick))
            } else {
                AddCardTile(pageModifier.bounceClick(pressedScale = 0.97f, onClick = onAddCard))
            }
        }

        // Page dots; the current one stretches into a pill
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(cards.size + 1) { index ->
                val selected = pagerState.currentPage == index
                val width by animateDpAsState(if (selected) 20.dp else 7.dp, label = "dot")
                val color by animateColorAsState(
                    if (selected) IncomeColor else MaterialTheme.colorScheme.outlineVariant,
                    label = "dotColor"
                )
                Box(
                    Modifier
                        .padding(horizontal = 3.dp)
                        .size(width = width, height = 7.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
private fun AddCardTile(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.586f)
            .clip(RoundedCornerShape(22.dp))
            .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(IncomeColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = IncomeColor)
            }
            Spacer(Modifier.height(8.dp))
            Text("Add a card", fontWeight = FontWeight.SemiBold)
        }
    }
}

// This month on the selected card: money in vs money out
@Composable
private fun MonthSummary(card: Card, modifier: Modifier = Modifier) {
    val totals = remember(card) { card.thisMonth() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryTile(
            label = if (card.credit) "Payments" else "Income",
            amount = totals.income,
            icon = Icons.Filled.ArrowDownward,
            color = IncomeColor,
            modifier = Modifier.weight(1f)
        )
        SummaryTile(
            label = "Spent",
            amount = totals.expense,
            icon = Icons.Filled.ArrowUpward,
            color = ExpenseColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryTile(label: String, amount: Long, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.10f))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(8.dp))
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        Spacer(Modifier.height(10.dp))
        AnimatedRupees(amount, format = ::rupees, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("This month", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable
private fun CenteredMessage(message: String, actionText: String, onAction: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(24.dp)) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onAction) { Text(actionText) }
        }
    }
}

// Tap to edit; swipe left to reveal delete (asks to confirm, so the row springs back)
@Composable
private fun TransactionRow(
    viewModel: ViewModel,
    transaction: Transaction,
    onClick: () -> Unit,
    onSwipeDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) onSwipeDelete()
            false
        }
    )
    val accent = if (transaction.income) IncomeColor else ExpenseColor

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        backgroundContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(DangerRed),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.White, modifier = Modifier.padding(end = 20.dp))
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .clickable(onClick = onClick)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painterResource(viewModel.categoryHash[transaction.type.id] ?: R.drawable.miscellaneous),
                    contentDescription = transaction.type.name,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    transaction.type.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    transaction.description.ifBlank { transaction.time },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                (if (transaction.income) "+" else "−") + rupees(transaction.amount.toLong()),
                fontWeight = FontWeight.SemiBold,
                color = if (transaction.income) PositiveGreen else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}