package com.example.expensetracker.Screens.Card

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.R
import com.example.expensetracker.Screens.common.CardFace
import com.example.expensetracker.Screens.common.SegmentedToggle
import com.example.expensetracker.Screens.common.networkLogoRes
import com.example.expensetracker.ViewModel.ViewModel
import com.example.expensetracker.dataclasses.Card
import com.example.expensetracker.dataclasses.CardNetwork
import com.example.expensetracker.ui.anim.appear
import com.example.expensetracker.ui.theme.ExpenseColor
import com.example.expensetracker.ui.theme.IncomeColor
import java.text.NumberFormat
import java.util.Locale

private const val CARD_DIGITS = 16
private val rupeeFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))

// Best guess from the first digits; the user can still pick another network
private fun detectNetwork(digits: String): CardNetwork? {
    val first2 = digits.take(2).toIntOrNull() ?: -1
    val first4 = digits.take(4).toIntOrNull() ?: -1
    return when {
        digits.startsWith("4") -> CardNetwork.Visa
        first2 == 34 || first2 == 37 -> CardNetwork.AmericanExpress
        first2 in 51..55 || first4 in 2221..2720 -> CardNetwork.MasterCard
        else -> null
    }
}

// "MMYY" digits → error message, or null if it's a real month
private fun expiryError(digits: String, label: String): String? = when {
    digits.length < 4 -> "Enter $label as MM/YY"
    digits.take(2).toInt() !in 1..12 -> "Month must be 01–12"
    else -> null
}

// MMYY → YYMM so dates compare correctly as numbers
private fun yearMonth(digits: String): Int = (digits.drop(2) + digits.take(2)).toInt()

/**
 * Add a card, or edit the selected one.
 * A live card preview at the top fills in as you type; fields check themselves as you go.
 */
@Composable
fun CardEditor(viewModel: ViewModel, isEdit: Boolean, onClose: () -> Unit) {
    val existing = viewModel.selectedCard.takeIf { isEdit }
    val haptics = LocalHapticFeedback.current
    val focus = LocalFocusManager.current

    // Plain digits are stored; the fields add the spaces and "/" for display
    var isCredit by rememberSaveable { mutableStateOf(existing?.credit ?: false) }
    var number by rememberSaveable { mutableStateOf(existing?.cardNo?.filter(Char::isDigit)?.take(CARD_DIGITS) ?: "") }
    var holder by rememberSaveable { mutableStateOf(existing?.cardHolder?.trim() ?: "") }
    var validFrom by rememberSaveable { mutableStateOf(existing?.validFrom?.filter(Char::isDigit)?.take(4) ?: "") }
    var validTo by rememberSaveable { mutableStateOf(existing?.validTo?.filter(Char::isDigit)?.take(4) ?: "") }
    var balance by rememberSaveable { mutableStateOf("") }
    var creditLimit by rememberSaveable { mutableStateOf(existing?.creditLimit?.takeIf { it > 0 }?.toString() ?: "") }
    var creditsUsed by rememberSaveable { mutableStateOf("") }
    var chosenNetwork by rememberSaveable {
        mutableStateOf(existing?.cardNetwork?.takeIf { it != CardNetwork.EmptyCard })
    }
    // Errors show for a field once it's been filled in, or for every field after a Save attempt
    var triedToSave by rememberSaveable { mutableStateOf(false) }

    val network = chosenNetwork ?: detectNetwork(number)
    val accent by animateColorAsState(if (isCredit) ExpenseColor else IncomeColor, tween(300), label = "accent")

    val alreadyUsed = existing?.creditsUsed ?: creditsUsed.toLongOrNull() ?: 0
    val errors = buildMap {
        if (number.length != CARD_DIGITS) put("number", "Card number must be $CARD_DIGITS digits")
        if (network == null) put("network", "Choose the card network")
        if (holder.isBlank()) put("holder", "Enter the name on the card")
        expiryError(validFrom, "valid from")?.let { put("from", it) }
        expiryError(validTo, "valid thru")?.let { put("to", it) }
        if ("from" !in this && "to" !in this && yearMonth(validTo) < yearMonth(validFrom)) {
            put("to", "Must be after Valid from")
        }
        if (isCredit) {
            val limit = creditLimit.toLongOrNull()
            when {
                limit == null || limit <= 0 -> put("limit", "Enter the credit limit")
                alreadyUsed > limit -> put("limit", "Can't be less than the ₹${rupeeFormat.format(alreadyUsed)} already used")
            }
        } else if (!isEdit && balance.isEmpty()) {
            put("balance", "Enter the current balance (0 is fine)")
        }
    }
    fun errorFor(key: String, filled: Boolean) = errors[key]?.takeIf { triedToSave || filled }

    fun save() {
        if (errors.isNotEmpty()) {
            triedToSave = true
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            viewModel.showMessage(errors.values.first())
            return
        }
        focus.clearFocus()
        val card = Card(
            cardNetwork = network!!,
            cardHolder = holder.trim(),
            cardNo = number,
            validFrom = "${validFrom.take(2)}/${validFrom.drop(2)}",
            validTo = "${validTo.take(2)}/${validTo.drop(2)}",
            debit = !isCredit,
            credit = isCredit,
            balance = existing?.balance ?: balance.toLongOrNull() ?: 0,
            creditLimit = if (isCredit) creditLimit.toLong() else 0,
            creditsUsed = if (isCredit) alreadyUsed else 0
        )
        viewModel.saveCard(card, editingId = existing?.cardId, onSaved = onClose)
    }

    BackHandler(onBack = onClose)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) { Icon(Icons.Filled.Close, contentDescription = "Close") }
            Text(
                if (isEdit) "Edit card" else "Add card",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Same card design as Home/Cards, filling in live as you type
            CardFace(
                isCredit = isCredit,
                numberText = (number + "•".repeat(CARD_DIGITS - number.length)).chunked(4).joinToString("  "),
                holder = holder,
                validToText = validTo.padEnd(4, '•').let { "${it.take(2)}/${it.drop(2)}" },
                network = network,
                amountLabel = if (isCredit) "Limit" else "Balance",
                amount = (if (isCredit) creditLimit else (existing?.balance?.toString() ?: balance)).toLongOrNull() ?: 0,
                animateAmount = false,
                modifier = Modifier.appear(fromScale = 0.92f)
            )

            Spacer(Modifier.height(20.dp))

            // Type can't change after the card exists (balance vs. limit work differently)
            if (!isEdit) {
                SegmentedToggle(
                    first = "Debit",
                    second = "Credit",
                    secondSelected = isCredit,
                    accent = accent,
                    onChange = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        isCredit = it
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally).appear(delayMillis = 80)
                )
                Spacer(Modifier.height(16.dp))
            }

            Field(
                value = number,
                onValueChange = { new -> number = new.filter(Char::isDigit).take(CARD_DIGITS) },
                label = "Card number",
                placeholder = "1234 5678 9012 3456",
                error = errorFor("number", number.length == CARD_DIGITS),
                keyboardType = KeyboardType.Number,
                visualTransformation = CardNumberTransformation,
                delayMillis = 120
            )

            // Network: detected from the number, or pick one
            Text(
                "Network",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.appear(delayMillis = 160)) {
                listOf(CardNetwork.Visa, CardNetwork.MasterCard, CardNetwork.AmericanExpress).forEach { option ->
                    NetworkChip(
                        network = option,
                        selected = network == option,
                        accent = accent,
                        onClick = { chosenNetwork = option }
                    )
                }
            }
            errorFor("network", false)?.let { ErrorText(it) }
            Spacer(Modifier.height(12.dp))

            Field(
                value = holder,
                onValueChange = { holder = it.take(26) },
                label = "Name on card",
                placeholder = "As printed on the card",
                error = errorFor("holder", false),
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Characters,
                delayMillis = 200
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Field(
                    value = validFrom,
                    onValueChange = { validFrom = it.filter(Char::isDigit).take(4) },
                    label = "Valid from",
                    placeholder = "MM/YY",
                    error = errorFor("from", validFrom.length == 4),
                    keyboardType = KeyboardType.Number,
                    visualTransformation = ExpiryTransformation,
                    delayMillis = 240,
                    modifier = Modifier.weight(1f)
                )
                Field(
                    value = validTo,
                    onValueChange = { validTo = it.filter(Char::isDigit).take(4) },
                    label = "Valid thru",
                    placeholder = "MM/YY",
                    error = errorFor("to", validTo.length == 4),
                    keyboardType = KeyboardType.Number,
                    visualTransformation = ExpiryTransformation,
                    delayMillis = 240,
                    modifier = Modifier.weight(1f)
                )
            }

            // Money fields swap when the type changes
            AnimatedContent(
                targetState = isCredit,
                transitionSpec = {
                    (fadeIn(tween(220)) + expandVertically()) togetherWith (fadeOut(tween(150)) + shrinkVertically())
                },
                label = "moneyFields"
            ) { credit ->
                Column {
                    if (credit) {
                        Field(
                            value = creditLimit,
                            onValueChange = { creditLimit = it.filter(Char::isDigit).take(9) },
                            label = "Credit limit",
                            placeholder = "e.g. 50000",
                            prefix = "₹ ",
                            error = errorFor("limit", creditLimit.isNotEmpty()),
                            keyboardType = KeyboardType.Number,
                            delayMillis = 280
                        )
                        if (!isEdit) {
                            Field(
                                value = creditsUsed,
                                onValueChange = { creditsUsed = it.filter(Char::isDigit).take(9) },
                                label = "Already spent (optional)",
                                placeholder = "0",
                                prefix = "₹ ",
                                error = null,
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done,
                                onDone = { save() },
                                delayMillis = 300
                            )
                        }
                    } else if (!isEdit) {
                        Field(
                            value = balance,
                            onValueChange = { balance = it.filter(Char::isDigit).take(9) },
                            label = "Current balance",
                            placeholder = "e.g. 25000",
                            prefix = "₹ ",
                            error = errorFor("balance", false),
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                            onDone = { save() },
                            delayMillis = 280
                        )
                    } else {
                        Text(
                            "Balance changes as you add income and expenses, so it isn't edited here.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        Button(
            onClick = { save() },
            enabled = !viewModel.isSavingCard,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = accent,
                disabledContainerColor = accent.copy(alpha = 0.6f),
                contentColor = Color.White,
                disabledContentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .height(56.dp)
        ) {
            AnimatedContent(targetState = viewModel.isSavingCard, label = "saveCard") { saving ->
                if (saving) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                } else {
                    Text(if (isEdit) "Save changes" else "Add card", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun Field(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    error: String?,
    keyboardType: KeyboardType,
    delayMillis: Int,
    modifier: Modifier = Modifier,
    prefix: String? = null,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    imeAction: ImeAction = ImeAction.Next,
    onDone: (() -> Unit)? = null
) {
    Column(modifier.appear(delayMillis = delayMillis, offsetY = 16.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            prefix = prefix?.let { { Text(it) } },
            isError = error != null,
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                capitalization = capitalization,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(onDone = { onDone?.invoke() }),
            modifier = Modifier.fillMaxWidth()
        )
        // Error slides in under the field instead of shifting things abruptly
        AnimatedVisibility(visible = error != null, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            ErrorText(error.orEmpty())
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun ErrorText(message: String) {
    Text(
        message,
        color = MaterialTheme.colorScheme.error,
        fontSize = 12.sp,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
private fun NetworkChip(network: CardNetwork, selected: Boolean, accent: Color, onClick: () -> Unit) {
    val border by animateColorAsState(
        if (selected) accent else MaterialTheme.colorScheme.outlineVariant,
        label = "networkBorder"
    )
    Box(
        modifier = Modifier
            .size(width = 76.dp, height = 46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) accent.copy(alpha = 0.10f) else Color.Transparent)
            .border(if (selected) 2.dp else 1.dp, border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(painterResource(networkLogoRes(network)), contentDescription = network.name, modifier = Modifier.size(40.dp))
    }
}

// Shows "1234567890123456" as "1234 5678 9012 3456" while the field keeps plain digits
private object CardNumberTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val out = digits.chunked(4).joinToString(" ")
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = offset + (offset - 1).coerceAtLeast(0) / 4
            override fun transformedToOriginal(offset: Int) = (offset - offset / 5).coerceIn(0, digits.length)
        }
        return TransformedText(AnnotatedString(out), mapping)
    }
}

// Shows "0329" as "03/29"
private object ExpiryTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val out = if (digits.length > 2) digits.take(2) + "/" + digits.drop(2) else digits
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = if (offset > 2 && digits.length > 2) offset + 1 else offset
            override fun transformedToOriginal(offset: Int) = (if (offset > 2) offset - 1 else offset).coerceIn(0, digits.length)
        }
        return TransformedText(AnnotatedString(out), mapping)
    }
}
