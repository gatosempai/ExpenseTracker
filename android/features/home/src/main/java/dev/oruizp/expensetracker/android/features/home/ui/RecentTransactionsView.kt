package dev.oruizp.expensetracker.android.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.oruizp.expensetracker.android.core.theme.ExpenseTrackerTheme
import dev.oruizp.expensetracker.android.core.utils.date.formatTime
import dev.oruizp.expensetracker.android.data.local.Expense
import dev.oruizp.expensetracker.android.data.local.ExpenseCategory
import java.util.Locale

@Composable
fun RecentTransactionsview(expenses: List<Expense>) {
    Column() {
        Text(
            text = "Recent Transactions",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(expenses) { expense ->
                ExpenseListItem(expense = expense)
            }
        }
    }
}

@Composable
fun ExpenseListItem(expense: Expense) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getCategoryColor(expense.category))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = expense.category.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatTime(expense.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatCurrency(expense.amount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReceentTrasactionsViewPreview() {
    val now = System.currentTimeMillis()
    val yesterday = now - 24 * 60 * 60 * 1000
    val lastWeek = now - 7 * 24 * 60 * 60 * 1000

    val sampleExpenses = listOf(
        Expense(1, "Groceries", 50.0, ExpenseCategory.FOOD, timestamp = now),
        Expense(2, "Bus Fare", 2.5, ExpenseCategory.TRANSPORT, timestamp = yesterday),
        Expense(3, "Movie", 15.0, ExpenseCategory.ENTERTAINMENT, timestamp = lastWeek),
        Expense(4, "Rent", 1200.0, ExpenseCategory.BILLS, timestamp = now)
    )
    ExpenseTrackerTheme {
        RecentTransactionsview(
            expenses = sampleExpenses
        )
    }
}