package com.example.expensetracker.Screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ViewModel.ViewModel
import com.example.expensetracker.dataclasses.Card
import com.example.expensetracker.ui.theme.ExpenseColor
import com.example.expensetracker.ui.theme.IncomeColor
import com.example.expensetracker.ui.theme.ScreenBackground

@Composable
fun AnalyticsScreen(
    vm: ViewModel,
) {

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground),

        contentPadding = PaddingValues(20.dp),

        verticalArrangement = Arrangement.spacedBy(20.dp)

    ) {

        item {

             AnalyticsHeader()

        }

        item {

            MonthSelector()

        }

        item {

            //SelectedCard(vm.selectedCard)

        }

        item {

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                SummaryCard(
                    modifier = Modifier,
                    title = "Income",
                    amount = "₹45,000",
                    color = IncomeColor
                )

                SummaryCard(
                    modifier = Modifier,
                    title = "Expense",
                    amount = "₹31,580",
                    color = ExpenseColor
                )

            }

        }

        item {

            NetBalanceCard()

        }

        }

    }

    @Composable
    fun AnalyticsHeader() {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Analytics",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your financial overview",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

        }

    }


@Composable
fun MonthSelector() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = { }) {
            Icon(
                Icons.Default.KeyboardArrowLeft,
                contentDescription = null
            )
        }

        Text(
            text = "June 2025",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        IconButton(onClick = { }) {
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null
            )
        }

    }
}


@Composable
fun SelectedCard(
    card: Card
) {

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = card.cardNetwork.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = card.cardHolder
            )

            Spacer(modifier = Modifier.height(4.dp))

            val lastFour =
                if (card.cardNo.length >= 4)
                    card.cardNo.takeLast(4)
                else
                    "----"

            Text(
                text = "•••• $lastFour",
                color = Color.Gray
            )

        }

    }

}


@Composable
fun SummarySection() {

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        SummaryCard(
            modifier = Modifier.weight(1f),
            title = "Income",
            amount = "₹45,000",
            color = IncomeColor
        )

        SummaryCard(
            modifier = Modifier.weight(1f),
            title = "Expense",
            amount = "₹31,580",
            color = ExpenseColor
        )

    }

}

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: String,
    color: Color
) {

    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(color)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = amount,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

        }

    }

}

@Composable
fun NetBalanceCard() {

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = "Net Balance",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "₹13,420",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "▲ +12% from last month",
                color = IncomeColor,
                fontWeight = FontWeight.Medium
            )

        }

    }

}