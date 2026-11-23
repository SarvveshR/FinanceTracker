package com.example.expensetracker.Screens.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.R
import com.example.expensetracker.dataclasses.Card
import com.example.expensetracker.dataclasses.CardNetwork
import com.example.expensetracker.ui.anim.AnimatedRupees
import com.example.expensetracker.ui.rupees

// The one card design used everywhere: Home carousel, Cards list and the card editor's live preview.
// Debit cards are purple, credit cards orange; real-card proportions (85.6 × 54 mm).

fun networkLogoRes(network: CardNetwork?): Int = when (network) {
    CardNetwork.MasterCard -> R.drawable.mastercard
    CardNetwork.AmericanExpress -> R.drawable.amex
    else -> R.drawable.visa
}

/** Only the last 4 digits are ever shown on a saved card. */
fun Card.maskedNumber(): String = "••••  ••••  ••••  " + cardNo.takeLast(4)

/** What can still be spent: balance for debit, limit minus used for credit. */
fun Card.available(): Long = if (debit) balance else creditLimit - creditsUsed

@Composable
fun CardFace(card: Card, modifier: Modifier = Modifier) {
    CardFace(
        isCredit = card.credit,
        numberText = card.maskedNumber(),
        holder = card.cardHolder,
        validToText = card.validTo,
        network = card.cardNetwork.takeIf { it != CardNetwork.EmptyCard },
        amountLabel = if (card.credit) "Available" else "Balance",
        amount = card.available(),
        modifier = modifier
    )
}

@Composable
fun CardFace(
    isCredit: Boolean,
    numberText: String,
    holder: String,
    validToText: String,
    network: CardNetwork?,
    amountLabel: String,
    amount: Long,
    modifier: Modifier = Modifier,
    animateAmount: Boolean = true
) {
    val start by animateColorAsState(if (isCredit) Color(0xFFFF9A62) else Color(0xFF8B5CFF), tween(400), label = "gradStart")
    val end by animateColorAsState(if (isCredit) Color(0xFFE5533D) else Color(0xFF3F1DB3), tween(400), label = "gradEnd")
    val shape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.586f)
            .clip(shape)
            .background(Brush.linearGradient(listOf(start, end)))
            .border(1.dp, Color.White.copy(alpha = 0.25f), shape)
    ) {
        // Decorative rings
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(Color.White.copy(alpha = 0.08f), radius = size.height * 0.75f, center = Offset(size.width, 0f))
            drawCircle(Color.White.copy(alpha = 0.06f), radius = size.height * 0.5f, center = Offset(size.width * 0.85f, size.height * 1.05f))
        }
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column {
                    Text(amountLabel.uppercase(), color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, letterSpacing = 1.5.sp)
                    if (animateAmount) {
                        AnimatedRupees(amount, format = ::rupees, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text(rupees(amount), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.weight(1f))
                // Logo pops in once the network is known, and swaps if it changes
                AnimatedContent(
                    targetState = network,
                    transitionSpec = { (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut()) },
                    label = "logo"
                ) { shown ->
                    if (shown != null) {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Image(painterResource(networkLogoRes(shown)), contentDescription = shown.name, modifier = Modifier.size(34.dp))
                        }
                    } else {
                        Spacer(Modifier.size(34.dp))
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            // Card chip
            Box(
                Modifier
                    .size(36.dp, 26.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFFFFE29A), Color(0xFFE0B04F))))
            )
            Spacer(Modifier.height(10.dp))
            Text(numberText, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, maxLines = 1)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Column(Modifier.weight(1f)) {
                    Text("CARD HOLDER", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp, letterSpacing = 1.5.sp)
                    Text(
                        holder.ifBlank { "YOUR NAME" }.uppercase(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("VALID THRU", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp, letterSpacing = 1.5.sp)
                    Text(validToText, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.size(width = 12.dp, height = 0.dp))
                Text(
                    if (isCredit) "CREDIT" else "DEBIT",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}
