package dev.oruizp.expensetracker.android.features.trasactions.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FilterChipRowView(
    items: List<String> = emptyList(),
    onItemClick: (String) -> Unit = {}
) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(items) { category ->
            ChipFilterItemCard(category = category, onItemClick = onItemClick)
        }
    }

}

@Composable
fun ChipFilterItemCard(
    category: String,
    onItemClick: (String) -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onItemClick(category) }
    ) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = category)
    }
}

@Preview(showBackground = true)
@Composable
fun FilterChipRowViewPreview() {
    val sampleCategories = listOf("Food", "Transport", "Shopping", "Bills", "Entertainment", "Other")
    FilterChipRowView(items = sampleCategories)
}