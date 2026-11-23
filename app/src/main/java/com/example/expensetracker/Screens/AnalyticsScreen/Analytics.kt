package com.example.expensetracker.Screens.analytics

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.key
import androidx.compose.ui.geometry.Size
import com.example.expensetracker.ui.anim.AnimatedRupees
import com.example.expensetracker.ui.anim.STAGGER_MILLIS
import com.example.expensetracker.ui.anim.appear
import com.example.expensetracker.ui.anim.rememberIntroPlaying
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ViewModel.ViewModel
import com.example.expensetracker.dataclasses.Transaction
import com.example.expensetracker.ui.theme.ExpenseColor
import com.example.expensetracker.ui.theme.IncomeColor
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as DateTextStyle
import java.util.Locale
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// Same pattern the save screen's date picker writes
private val transactionDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
private val monthFormat = DateTimeFormatter.ofPattern("MMMM yyyy")
private val dayFormat = DateTimeFormatter.ofPattern("d MMM yyyy")
private val rupeeFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))

private fun rupees(amount: Long) = "₹" + rupeeFormat.format(amount)

private val SliceColors = listOf(
    ExpenseColor,
    Color(0xFF2E2A47),
    IncomeColor,
    Color(0xFF2EC4B6),
    Color(0xFFFFB627),
    Color(0xFF4D96FF)
)
private val OthersColor = Color(0xFFB8B8C7)

private data class DatedTransaction(val date: LocalDate, val transaction: Transaction)

private data class CategorySlice(
    val name: String,
    val amount: Long,
    val fraction: Float,
    val color: Color
)

@Composable
fun AnalyticsScreen(
    vm: ViewModel,
) {
    val state by vm.cards
    val introPlaying = rememberIntroPlaying()
    var month by remember { mutableStateOf(YearMonth.now()) }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }

    // Every transaction across all cards, with its date parsed once
    val transactions = remember(state.cards) {
        state.cards.flatMap { it.transaction }.mapNotNull { t ->
            runCatching { LocalDate.parse(t.date, transactionDateFormat) }.getOrNull()
                ?.let { DatedTransaction(it, t) }
        }
    }

    val inMonth = transactions.filter { YearMonth.from(it.date) == month }
    // Tapping a day narrows everything below the calendar to that day
    val inPeriod = selectedDay?.let { day -> inMonth.filter { it.date == day } } ?: inMonth
    val expenses = inPeriod.map { it.transaction }.filter { it.expense }
    val spent = expenses.sumOf { it.amount.toLong() }
    val earned = inPeriod.map { it.transaction }.filter { it.income }.sumOf { it.amount.toLong() }
    val expenseDays = inMonth.filter { it.transaction.expense }.map { it.date }.toSet()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = "Analytics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        when {
            state.error != null -> item {
                ErrorState(message = state.error!!, onRetry = { vm.getCards() })
            }

            state.loading -> item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ExpenseColor)
                }
            }

            else -> {
                item {
                    Box(Modifier.appear(enabled = introPlaying)) {
                    CalendarCard(
                        month = month,
                        selectedDay = selectedDay,
                        expenseDays = expenseDays,
                        onMonthChange = {
                            month = it
                            selectedDay = null
                        },
                        onDayClick = { day ->
                            selectedDay = if (selectedDay == day) null else day
                        }
                    )
                    }
                }

                item {
                    Box(Modifier.appear(delayMillis = 100, enabled = introPlaying)) {
                    SpendSummaryCard(
                        spent = spent,
                        earned = earned,
                        period = selectedDay?.let { "on ${it.format(dayFormat)}" }
                            ?: if (month == YearMonth.now()) "this month" else "in ${month.format(monthFormat)}",
                        dateLabel = selectedDay?.format(dayFormat) ?: month.format(monthFormat)
                    )
                    }
                }

                item {
                    Text(
                        text = "Spending by category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.appear(delayMillis = 180, enabled = introPlaying)
                    )
                }

                item {
                    Box(Modifier.appear(delayMillis = 240, enabled = introPlaying)) {
                        CategoryBreakdownCard(slices = categorySlices(expenses))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarCard(
    month: YearMonth,
    selectedDay: LocalDate?,
    expenseDays: Set<LocalDate>,
    onMonthChange: (YearMonth) -> Unit,
    onDayClick: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val days = (1..month.lengthOfMonth()).map { month.atDay(it) }
    val listState = rememberLazyListState()

    // Open on today when looking at the current month, otherwise on the 1st
    LaunchedEffect(month) {
        val focusIndex = if (YearMonth.from(today) == month) today.dayOfMonth - 1 else 0
        listState.scrollToItem((focusIndex - 3).coerceAtLeast(0))
    }

    AnalyticsCard {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onMonthChange(month.minusMonths(1)) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous month"
                    )
                }
                Text(
                    text = month.format(monthFormat),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onMonthChange(month.plusMonths(1)) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next month"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                state = listState,
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(days) { day ->
                    DayCell(
                        day = day,
                        isSelected = day == selectedDay,
                        isToday = day == today,
                        hasExpense = day in expenseDays,
                        onClick = { onDayClick(day) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    hasExpense: Boolean,
    onClick: () -> Unit
) {
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    val background by animateColorAsState(if (isSelected) ExpenseColor else Color.Transparent, label = "dayBg")

    Column(
        modifier = Modifier
            .width(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.dayOfWeek.getDisplayName(DateTextStyle.NARROW, Locale.getDefault()),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = day.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isToday && !isSelected) ExpenseColor else textColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Dot marks days that have at least one expense
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(
                    when {
                        !hasExpense -> Color.Transparent
                        isSelected -> Color.White
                        else -> ExpenseColor
                    }
                )
        )
    }
}

@Composable
private fun SpendSummaryCard(
    spent: Long,
    earned: Long,
    period: String,
    dateLabel: String
) {
    AnalyticsCard {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "You have spent", style = MaterialTheme.typography.bodyLarge)
                    AnimatedRupees(
                        value = spent,
                        format = ::rupees,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = ExpenseColor
                    )
                    Text(text = period, style = MaterialTheme.typography.bodyLarge)
                }
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (earned == 0L) {
                Text(
                    text = "No income recorded for this period",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                return@Column
            }

            // How much of the period's income has been spent
            val spentRatio = spent.toFloat() / earned
            val barFill = spentRatio.coerceIn(0f, 1f)
            val animatedFill by animateFloatAsState(
                targetValue = barFill,
                animationSpec = tween(900, easing = FastOutSlowInEasing),
                label = "bar"
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedFill)
                            .background(IncomeColor)
                    )
                    Text(
                        text = percent(spentRatio),
                        color = if (barFill > 0.25f) Color.White else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 10.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = percent(1f - barFill),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (spent > earned) {
                    "${rupees(spent - earned)} more than your income of ${rupees(earned)}"
                } else {
                    "of your ${rupees(earned)} income spent"
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (spent > earned) ExpenseColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CategoryBreakdownCard(slices: List<CategorySlice>) {
    AnalyticsCard {
        if (slices.isEmpty()) {
            Text(
                text = "No expenses in this period",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp)
            )
            return@AnalyticsCard
        }

        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PieChart(slices = slices, modifier = Modifier.size(220.dp))

            Spacer(modifier = Modifier.height(20.dp))

            slices.forEachIndexed { position, slice ->
                key(slice.name) {
                Row(
                    modifier = Modifier
                        .appear(delayMillis = 300 + position * STAGGER_MILLIS, offsetY = 12.dp)
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(slice.color)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = slice.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = rupees(slice.amount),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${(slice.fraction * 100).roundToInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(48.dp)
                    )
                }
                }
            }
        }
    }
}

@Composable
private fun PieChart(slices: List<CategorySlice>, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    val separator = MaterialTheme.colorScheme.surfaceContainer

    // Redraws from nothing each time the month/day (and so the slices) change
    val progress = remember { Animatable(0f) }
    LaunchedEffect(slices) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
    }

    Canvas(modifier = modifier) {
        val p = progress.value
        val radius = size.minDimension / 2 * (0.85f + 0.15f * p)
        var startAngle = -90f - (1f - p) * 90f

        slices.forEach { slice ->
            val sweep = slice.fraction * 360f * p
            drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            // White edges separate neighbouring slices
            if (slices.size > 1) {
                drawArc(
                    color = separator,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Slices too thin for a label are covered by the legend
            // Labels wait until the slices have nearly finished growing
            if (slice.fraction >= 0.07f && p > 0.9f) {
                val midAngle = Math.toRadians((startAngle + sweep / 2).toDouble())
                val label = textMeasurer.measure("${(slice.fraction * 100).roundToInt()}%", labelStyle)
                val labelCenter = Offset(
                    x = center.x + radius * 0.62f * cos(midAngle).toFloat(),
                    y = center.y + radius * 0.62f * sin(midAngle).toFloat()
                )
                drawText(
                    textLayoutResult = label,
                    topLeft = Offset(
                        labelCenter.x - label.size.width / 2f,
                        labelCenter.y - label.size.height / 2f
                    )
                )
            }

            startAngle += sweep
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Couldn't load your analytics",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        TextButton(onClick = onRetry) {
            Text("Retry", color = ExpenseColor)
        }
    }
}

@Composable
private fun AnalyticsCard(content: @Composable () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        content()
    }
}

// Largest categories first; anything past the palette is grouped into "Others"
private fun categorySlices(expenses: List<Transaction>): List<CategorySlice> {
    val total = expenses.sumOf { it.amount.toLong() }
    if (total == 0L) return emptyList()

    val byCategory = expenses
        .groupBy { it.type.name.ifBlank { "Uncategorised" } }
        .map { (name, list) -> name to list.sumOf { it.amount.toLong() } }
        .sortedByDescending { it.second }

    val grouped = byCategory.size > SliceColors.size
    val shown = if (grouped) {
        byCategory.take(SliceColors.size - 1) +
            ("Others" to byCategory.drop(SliceColors.size - 1).sumOf { it.second })
    } else {
        byCategory
    }

    return shown.mapIndexed { index, (name, amount) ->
        CategorySlice(
            name = name,
            amount = amount,
            fraction = amount.toFloat() / total,
            color = if (grouped && index == shown.lastIndex) OthersColor else SliceColors[index]
        )
    }
}

private fun percent(ratio: Float) = String.format(Locale.US, "%.1f%%", ratio * 100)