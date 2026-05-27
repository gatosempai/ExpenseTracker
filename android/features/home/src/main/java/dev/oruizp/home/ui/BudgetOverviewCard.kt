package dev.oruizp.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.oruizp.core.theme.CategoryBills
import dev.oruizp.core.theme.CategoryEntertainment
import dev.oruizp.core.theme.CategoryFood
import dev.oruizp.core.theme.CategoryOther
import dev.oruizp.core.theme.CategoryShopping
import dev.oruizp.core.theme.CategoryTransport
import dev.oruizp.core.theme.ExpenseTrackerTheme
import dev.oruizp.home.data.BudgetCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetOverviewCard(
    expenses: List<BudgetCategory>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Budget Overview",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp, bottom = 16.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(expenses) { category ->
                BudgetCardItem(category = category)
            }
        }
    }
}

@Composable
fun BudgetCardItem(category: BudgetCategory) {
    val progress = (category.spent / category.limit).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .width(120.dp)
            .height(72.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 12.sp,
                maxLines = 1
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = category.color,
                trackColor = category.color.copy(alpha = 0.2f)
            )

            // Spent / limit amount
            Text(
                text = "$${category.spent} / $${category.limit}",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetOverViewPreview() {
    val sampleBudgetCategory = listOf(
        BudgetCategory("Food", 50.0f, 100.0f, CategoryFood),
        BudgetCategory("Transport", 20.0f, 100.0f, CategoryTransport),
        BudgetCategory("Shopping", 100.0f, 100.0f, CategoryShopping),
        BudgetCategory("Bills", 10.0f, 100.0f, CategoryBills),
        BudgetCategory("Entertainment", 15.0f, 100.0f, CategoryEntertainment),
        BudgetCategory("Other", 10.0f, 100.0f, CategoryOther)
    )
    ExpenseTrackerTheme {
        BudgetOverviewCard(
            expenses = sampleBudgetCategory
        )
    }
}