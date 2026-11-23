package com.example.expensetracker.Screens.AddScreen

import com.example.expensetracker.ui.theme.DangerRed
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.R
import com.example.expensetracker.Screens.common.SegmentedToggle
import com.example.expensetracker.ViewModel.ViewModel
import com.example.expensetracker.dataclasses.Card
import com.example.expensetracker.dataclasses.Categories
import com.example.expensetracker.dataclasses.CardNetwork
import com.example.expensetracker.ui.anim.appear
import com.example.expensetracker.ui.theme.ExpenseColor
import com.example.expensetracker.ui.theme.IncomeColor
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
private val timeFormat = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
private val chipDateFormat = DateTimeFormatter.ofPattern("d MMM")
private val rupeeFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))
private fun rupees(amount: Long) = "₹" + rupeeFormat.format(amount)

// What the amount would do to the card, shown live under the amount
private data class Impact(val text: String, val isProblem: Boolean)

/**
 * One screen for adding or editing a transaction:
 * type toggle → card → amount (keypad) → category → optional date/time/note → Save.
 * The common case is 3 taps after "+": amount, category, Save.
 */
@Composable
fun TransactionEditor(
    viewModel: ViewModel,
    isEdit: Boolean,
    onClose: () -> Unit,
    goToAddCardScreen: () -> Unit
) {
    val state by viewModel.cards
    val card = viewModel.selectedCard
    val draft = viewModel.transaction
    val isIncome = draft.income
    val haptics = LocalHapticFeedback.current

    // Expense is orange, income/payment purple; everything accent-coloured follows the toggle
    val accent by animateColorAsState(if (isIncome) IncomeColor else ExpenseColor, tween(300), label = "accent")
    val incomeLabel = if (card.credit) "Payment" else "Income"

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showNote by remember { mutableStateOf(false) }

    BackHandler(onBack = onClose)

    if (card.cardId == null) {
        NoCardState(onClose, goToAddCardScreen)
        return
    }

    // In edit mode only the difference from the original amount changes the card
    val original = if (isEdit) card.transaction.getOrNull(viewModel.transactionIndex) else null
    val impact = impactOf(card, isIncome, draft.amount.toLong() - (original?.amount ?: 0).toLong(), draft.amount)
    val canSave = draft.amount > 0 && draft.type.id.isNotBlank() && impact?.isProblem != true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top: close + type toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) { Icon(Icons.Filled.Close, contentDescription = "Close") }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                if (isEdit) {
                    Text(
                        "Edit ${if (isIncome) incomeLabel.lowercase() else "expense"}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    SegmentedToggle(
                        first = "Expense",
                        second = incomeLabel,
                        secondSelected = isIncome,
                        accent = accent,
                        onChange = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.setTransactionType(it)
                        }
                    )
                }
            }
            Spacer(Modifier.width(48.dp)) // balances the close button so the toggle is centred
        }

        // Which card (only when adding; an edit stays on its card)
        if (!isEdit && state.cards.size > 1) {
            CardPicker(
                cards = state.cards,
                selectedIndex = viewModel.selectedCardIndex,
                accent = accent,
                onSelect = { viewModel.selectCard(it) }
            )
        } else {
            Text(
                "${if (card.credit) "Credit" else "Debit"} •••• ${card.cardNo.takeLast(4)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Amount, centred in the free space
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AmountDisplay(viewModel.expense, accent)
            Spacer(Modifier.height(6.dp))
            AnimatedContent(
                targetState = impact,
                transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
                label = "impact"
            ) { shown ->
                Text(
                    shown?.text ?: " ",
                    color = if (shown?.isProblem == true) DangerRed else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = if (shown?.isProblem == true) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }

        // Category
        Text(
            if (draft.amount > 0 && draft.type.id.isBlank()) "Choose a category" else "Category",
            color = if (draft.amount > 0 && draft.type.id.isBlank()) accent else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 20.dp, bottom = 6.dp)
        )
        CategoryRow(
            categories = if (isIncome) viewModel.incomeList else viewModel.expenseList,
            selectedId = draft.type.id,
            accent = accent,
            iconFor = { viewModel.categoryHash[it] ?: R.drawable.miscellaneous },
            onSelect = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.transaction = viewModel.transaction.copy(type = it)
            }
        )

        // Details: date, time, note
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DetailChip(Icons.Filled.CalendarToday, friendlyDate(draft.date)) { showDatePicker = true }
            DetailChip(Icons.Filled.Schedule, draft.time.ifEmpty { "Time" }) { showTimePicker = true }
            DetailChip(Icons.Filled.EditNote, draft.description.ifEmpty { "Add note" }, maxWidthDp = 160) { showNote = true }
        }

        NumberPad(
            onDigit = { viewModel.appendAmountDigit(it) },
            onDoubleZero = {
                viewModel.appendAmountDigit(0)
                viewModel.appendAmountDigit(0)
            },
            onBackspace = { viewModel.removeAmountDigit() },
            onClear = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.clearAmount()
            }
        )

        // Save
        val buttonColor by animateColorAsState(if (canSave) accent else accent.copy(alpha = 0.35f), label = "saveColor")
        Button(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                val saved = if (isEdit) viewModel.saveEditedTransaction() else viewModel.saveNewTransaction()
                if (saved) onClose()
            },
            enabled = canSave,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                disabledContainerColor = buttonColor,
                contentColor = Color.White,
                disabledContentColor = Color.White.copy(alpha = 0.8f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .height(56.dp)
        ) {
            Text(
                when {
                    isEdit -> "Update"
                    isIncome -> "Save ${incomeLabel.lowercase()}"
                    else -> "Save expense"
                },
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    if (showDatePicker) {
        DateDialog(
            initial = runCatching { LocalDate.parse(draft.date, dateFormat) }.getOrDefault(LocalDate.now()),
            onPicked = { viewModel.transaction = viewModel.transaction.copy(date = it.format(dateFormat)) },
            onDismiss = { showDatePicker = false }
        )
    }
    if (showTimePicker) {
        TimeDialog(
            initial = runCatching { LocalTime.parse(draft.time, timeFormat) }.getOrDefault(LocalTime.now()),
            onPicked = { viewModel.transaction = viewModel.transaction.copy(time = it.format(timeFormat)) },
            onDismiss = { showTimePicker = false }
        )
    }
    if (showNote) {
        NoteDialog(
            initial = draft.description,
            onSave = { viewModel.transaction = viewModel.transaction.copy(description = it.trim()) },
            onDismiss = { showNote = false }
        )
    }
}

// Live "what happens to the card" line. delta = how much this changes the card (the full amount when adding).
private fun impactOf(card: Card, isIncome: Boolean, delta: Long, amount: Int): Impact? {
    if (amount <= 0) {
        return when {
            card.debit -> Impact("Balance ${rupees(card.balance)}", false)
            else -> Impact("Credit left ${rupees(card.creditLimit - card.creditsUsed)}", false)
        }
    }
    return when {
        card.debit && !isIncome -> {
            val after = card.balance - delta
            if (after < 0) Impact("Not enough balance (${rupees(card.balance)} available)", true)
            else Impact("Balance after: ${rupees(after)}", false)
        }
        card.debit -> Impact("Balance after: ${rupees(card.balance + delta)}", false)
        !isIncome -> {
            val left = card.creditLimit - card.creditsUsed - delta
            if (left < 0) Impact("Over the credit limit by ${rupees(-left)}", true)
            else Impact("Credit left after: ${rupees(left)}", false)
        }
        else -> {
            val owed = card.creditsUsed - delta
            if (owed < 0) Impact("More than the ${rupees(card.creditsUsed)} owed", true)
            else Impact("Owed after: ${rupees(owed)}", false)
        }
    }
}

private fun friendlyDate(date: String): String {
    val parsed = runCatching { LocalDate.parse(date, dateFormat) }.getOrNull() ?: return "Date"
    val today = LocalDate.now()
    return when (parsed) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> parsed.format(chipDateFormat)
    }
}

@Composable
private fun NoCardState(onClose: () -> Unit, goToAddCardScreen: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        IconButton(onClick = onClose, modifier = Modifier.padding(8.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
        Column(
            Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Add a card first, then record its income and expenses.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Button(onClick = goToAddCardScreen) { Text("Add card") }
        }
    }
}

@Composable
private fun CardPicker(cards: List<Card>, selectedIndex: Int, accent: Color, onSelect: (Int) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(cards) { index, card ->
            val selected = index == selectedIndex
            val border by animateColorAsState(if (selected) accent else Color.Transparent, label = "cardBorder")
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (selected) accent.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.5.dp, border, RoundedCornerShape(14.dp))
                    .clickable { onSelect(index) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painterResource(
                        when (card.cardNetwork) {
                            CardNetwork.MasterCard -> R.drawable.mastercard
                            CardNetwork.AmericanExpress -> R.drawable.amex
                            else -> R.drawable.visa
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("•••• ${card.cardNo.takeLast(4)}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(
                        if (card.debit) rupees(card.balance) else "${rupees(card.creditLimit - card.creditsUsed)} left",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// Big amount; each digit rolls in (up when typing, down when deleting)
@Composable
private fun AmountDisplay(amount: String, accent: Color) {
    val formatted = amount.toLongOrNull()?.let { rupeeFormat.format(it) } ?: "0"
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            "₹",
            fontSize = 34.sp,
            fontWeight = FontWeight.Medium,
            color = if (amount.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else accent
        )
        Spacer(Modifier.width(6.dp))
        AnimatedContent(
            targetState = formatted,
            transitionSpec = {
                val growing = targetState.length >= initialState.length
                (slideInVertically { if (growing) it / 2 else -it / 2 } + fadeIn(tween(150))) togetherWith
                    (slideOutVertically { if (growing) -it / 2 else it / 2 } + fadeOut(tween(120)))
            },
            label = "amount"
        ) { shown ->
            Text(
                shown,
                fontSize = if (shown.length > 9) 40.sp else 56.sp,
                fontWeight = FontWeight.Bold,
                color = if (amount.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun CategoryRow(
    categories: List<Categories>,
    selectedId: String,
    accent: Color,
    iconFor: (String) -> Int,
    onSelect: (Categories) -> Unit
) {
    val listState = rememberLazyListState()
    // Editing an existing transaction: bring its category into view
    LaunchedEffect(categories) {
        val index = categories.indexOfFirst { it.id == selectedId }
        if (index > 0) listState.scrollToItem(index)
    }
    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(categories, key = { _, c -> c.id }) { position, category ->
            val selected = category.id == selectedId
            val background by animateColorAsState(
                if (selected) accent.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                label = "chipBg"
            )
            val border by animateColorAsState(if (selected) accent else Color.Transparent, label = "chipBorder")
            val scale by animateFloatAsState(
                if (selected) 1.05f else 1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "chipScale"
            )
            Row(
                modifier = Modifier
                    .appear(delayMillis = position * 30, offsetY = 10.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .background(background)
                    .border(1.5.dp, border, RoundedCornerShape(20.dp))
                    .clickable { onSelect(category) }
                    .padding(start = 6.dp, end = 14.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painterResource(iconFor(category.id)),
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    category.name,
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) accent else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun DetailChip(icon: ImageVector, label: String, maxWidthDp: Int = 140, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            // long notes get cut off instead of pushing the other chips away
            modifier = Modifier.widthIn(max = maxWidthDp.dp)
        )
    }
}

// 1–9, 00, 0, ⌫ (long-press ⌫ clears)
@Composable
private fun NumberPad(
    onDigit: (Int) -> Unit,
    onDoubleZero: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val rows = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf("00", "0", "⌫"))
    Column(Modifier.padding(horizontal = 12.dp)) {
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { key ->
                    PadKey(
                        label = key,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            when (key) {
                                "⌫" -> onBackspace()
                                "00" -> onDoubleZero()
                                else -> onDigit(key.toInt())
                            }
                        },
                        onLongClick = if (key == "⌫") onClear else null
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PadKey(label: String, modifier: Modifier, onClick: () -> Unit, onLongClick: (() -> Unit)?) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) 0.88f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
        label = "key"
    )
    Box(
        modifier = modifier
            .height(58.dp)
            .padding(3.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                interactionSource = interaction,
                indication = ripple(),
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (label == "⌫") {
            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Delete (hold to clear)")
        } else {
            Text(label, fontSize = 26.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateDialog(initial: LocalDate, onPicked: (LocalDate) -> Unit, onDismiss: () -> Unit) {
    // The Material date picker works in UTC milliseconds
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initial.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let {
                    onPicked(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
                }
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    ) {
        DatePicker(state = state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeDialog(initial: LocalTime, onPicked: (LocalTime) -> Unit, onDismiss: () -> Unit) {
    val state = rememberTimePickerState(initialHour = initial.hour, initialMinute = initial.minute, is24Hour = false)
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onPicked(LocalTime.of(state.hour, state.minute))
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        text = { TimePicker(state = state) }
    )
}

@Composable
private fun NoteDialog(initial: String, onSave: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Note") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { if (it.length <= 120) text = it },
                placeholder = { Text("e.g. Dinner with friends") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(text)
                onDismiss()
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
