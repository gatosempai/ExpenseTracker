package dev.oruizp.expensetracker.android.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.oruizp.expensetracker.android.core.theme.ExpenseTrackerTheme
import dev.oruizp.expensetracker.android.features.home.data.TotalSpentUiState
import kotlin.math.abs

@Composable
fun TotalSpentCard(
    totalSpent: TotalSpentUiState,
    percentage: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Spent",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            when (totalSpent) {
                is TotalSpentUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is TotalSpentUiState.Success -> {
                    Text(
                        text = formatCurrency(totalSpent.totalSpent.toDouble()),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                else -> {
                    Text(
                        text = (totalSpent as TotalSpentUiState.Error).message,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            ComparisonBadgeView(percentage)
        }
    }
}

@Composable
fun ComparisonBadgeView(
    percentage: Double,
    comparisonText: String = "vs last month",
    modifier: Modifier = Modifier
) {
    val isPositive = percentage > 0
    val arrow = if (isPositive) "▲" else "▼"
    val percentageFormatted = String.format("%.0f", abs(percentage))
    val badgeColor = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)

    Row(
        modifier = modifier
            .background(
                color = badgeColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$arrow $percentageFormatted%",
            color = badgeColor,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = " $comparisonText",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TotalSpentPreview() {
    ExpenseTrackerTheme {
        TotalSpentCard(TotalSpentUiState.Success("1250.00"), -12.0)
    }
}