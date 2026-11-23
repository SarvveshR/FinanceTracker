package com.example.expensetracker.ui

import com.example.expensetracker.dataclasses.Card
import com.example.expensetracker.dataclasses.Transaction
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

// Formatting and date helpers shared by the screens

private val rupeeFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))

/** ₹1,25,000 (Indian digit grouping) */
fun rupees(amount: Long): String = (if (amount < 0) "-₹" else "₹") + rupeeFormat.format(kotlin.math.abs(amount))

private val transactionDate = DateTimeFormatter.ofPattern("dd-MM-yyyy")

// Older rows were saved as "05:24 pm", newer ones as "05:24 PM"
private val transactionTime = DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .appendPattern("hh:mm a")
    .toFormatter(Locale.ENGLISH)

fun Transaction.localDate(): LocalDate? = runCatching { LocalDate.parse(date, transactionDate) }.getOrNull()

fun Transaction.localDateTime(): LocalDateTime? {
    val day = localDate() ?: return null
    val time = runCatching { LocalTime.parse(time, transactionTime) }.getOrDefault(LocalTime.MIDNIGHT)
    return day.atTime(time)
}

/** "Today", "Yesterday", or "Wed, 12 Nov" */
fun friendlyDay(day: LocalDate?): String {
    if (day == null) return "Undated"
    val today = LocalDate.now()
    return when (day) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> day.format(DateTimeFormatter.ofPattern(if (day.year == today.year) "EEE, d MMM" else "d MMM yyyy"))
    }
}

data class MonthTotals(val income: Long, val expense: Long)

/** Money in and out on this card during the current month */
fun Card.thisMonth(): MonthTotals {
    val month = YearMonth.now()
    val rows = transaction.filter { t -> t.localDate()?.let { YearMonth.from(it) == month } == true }
    return MonthTotals(
        income = rows.filter { it.income }.sumOf { it.amount.toLong() },
        expense = rows.filter { it.expense }.sumOf { it.amount.toLong() }
    )
}
