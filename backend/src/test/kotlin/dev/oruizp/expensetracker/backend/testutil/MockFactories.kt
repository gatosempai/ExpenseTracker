package dev.oruizp.expensetracker.backend.testutil

import dev.oruizp.expensetracker.backend.models.*

object MockFactories {
    fun createExpenseInput(
        title: String = "Test Expense",
        amount: Double = 50.0,
        categoryId: String = "1",
        date: String = "2026-05-25",
    ) = CreateExpenseInput(
        title = title,
        amount = amount,
        categoryId = categoryId,
        date = date,
    )

    fun createCategoryInput(
        name: String = "Test Category",
        color: String = "#FF0000",
        icon: String? = "TEST",
    ) = CreateCategoryInput(
        name = name,
        color = color,
        icon = icon,
    )

    fun createBudgetInput(
        categoryId: String = "1",
        limit: Double = 1000.0,
        period: Period = Period.MONTHLY,
    ) = CreateBudgetInput(
        categoryId = categoryId,
        limit = limit,
        period = period,
    )
}
