package dev.oruizp.expensetracker.android.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.oruizp.expensetracker.android.core.theme.ExpenseTrackerTheme
import dev.oruizp.expensetracker.android.features.home.ui.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme {
                HomeScreen()
            }
        }
    }
}