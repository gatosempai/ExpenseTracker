package dev.oruizp.expensetracker.backend.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.*

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255)
    val passwordHash = varchar("password_hash", 255)
    val displayName = varchar("display_name", 100)
    val preferences = text("preferences").nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}

object CategoriesTable : Table("categories") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(UsersTable.id).nullable()
    val name = varchar("name", 100)
    val color = varchar("color", 9)
    val icon = varchar("icon", 50)
    val isDefault = bool("is_default").default(false)
    val sortOrder = integer("sort_order").default(0)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}

object ExpensesTable : Table("expenses") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(UsersTable.id)
    val title = varchar("title", 255)
    val amount = double("amount")
    val categoryId = integer("category_id").references(CategoriesTable.id)
    val expenseDate = date("date")
    val description = text("description").nullable()
    val receiptUrl = varchar("receipt_url", 500).nullable()
    val locationLat = double("location_lat").nullable()
    val locationLng = double("location_lng").nullable()
    val conflictVersion = integer("conflict_version").default(1)
    val clientId = varchar("client_id", 100).nullable()
    val isDeleted = bool("is_deleted").default(false)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}

object BudgetsTable : Table("budgets") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(UsersTable.id)
    val categoryId = integer("category_id").references(CategoriesTable.id)
    val limit = double("budget_limit")
    val period = varchar("period", 10)
    val startDate = date("start_date")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}

object SyncLogTable : Table("sync_log") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(UsersTable.id)
    val clientId = varchar("client_id", 100)
    val operationType = varchar("operation_type", 10)
    val entityType = varchar("entity_type", 20)
    val entityId = integer("entity_id").nullable()
    val clientEntityId = varchar("client_entity_id", 100)
    val payload = text("payload")
    val conflictOccurred = bool("conflict_occurred").default(false)
    val resolution = varchar("resolution", 10).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}
