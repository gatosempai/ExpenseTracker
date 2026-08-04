package dev.oruizp.expensetracker.android.features.trasactions.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.oruizp.expensetracker.android.core.theme.ExpenseTrackerTheme

@Composable
fun TransactionsScreen() {

    TransactionsScreenContent()
}

@Composable
fun TransactionsScreenContent() {
    Scaffold(

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FilterChipRowView()

            TransactionsSearchView()

        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionsScreenPreview() {
    val sampleCategories = listOf("Food", "Transport", "Shopping", "Bills", "Entertainment", "Other")

    ExpenseTrackerTheme {
        TransactionsScreen(sampleCategories)
    }
}