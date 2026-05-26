# ExpenseTracker — Backend

![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin)
![Ktor](https://img.shields.io/badge/Ktor-2.3.12-007396?logo=ktor)
![GraphQL](https://img.shields.io/badge/GraphQL-E10098?logo=graphql)
![Exposed](https://img.shields.io/badge/Exposed-0.53.0-00897B)
![License](https://img.shields.io/badge/License-MIT-yellow)

GraphQL-first Ktor backend for the ExpenseTracker Android app. Handles expense CRUD, budgets, categories, analytics reports, and offline-first sync.

---

## Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [GraphQL Schema](#graphql-schema)
- [REST Endpoints](#rest-endpoints)
- [Data Models](#data-models)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [SOLID & Best Practices](#solid--best-practices)
- [Error Handling](#error-handling)
- [Security](#security)
- [Database](#database)
- [Build & Run](#build--run)
- [Testing Strategy](#testing-strategy)
- [Future Expansion](#future-expansion)

---

## Overview

The backend provides a **GraphQL-first API** that powers all Android client features. REST is reserved for health checks and file uploads only.

| Client Feature | Backend API | GraphQL Operation |
|---------------|-------------|-------------------|
| Home Dashboard | Total spent + recent txs + budget overview | `query { dashboard }` |
| All Transactions | Filtered, paginated expense list | `query { expenses }` |
| Transaction Detail | Single expense by ID | `query { expense(id) }` |
| Add/Edit/Delete Expense | Mutations | `mutation { createExpense / updateExpense / deleteExpense }` |
| Budgets | CRUD + progress calculation | `query { budgets } mutation { createBudget / updateBudget / deleteBudget }` |
| Reports | Aggregated spending analytics | `query { reportSummary / reportMonthly / reportYearly }` |
| Categories | CRUD | `query { categories } mutation { createCategory / updateCategory / deleteCategory }` |
| Offline Sync | Batch mutations with conflict resolution | `mutation { syncBatch }` |
| CSV Export/Import | File download/upload (REST) | `POST /data/export` `POST /data/import` |

### Interaction with Android Client

```
┌──────────────────────┐         GraphQL (Apollo KMP)        ┌──────────────────────┐
│                      │  ──── POST /graphql ──────────────► │                      │
│   Android App        │  ◄──── JSON response ─────────────── │   Ktor Backend      │
│   (Apollo Client)    │                                      │   (graphql-kotlin)   │
│                      │  ──── WebSocket /graphql/ws ──────► │                      │
│                      │  ◄──── Subscriptions ─────────────── │                      │
└──────────────────────┘                                      └──────────────────────┘
         │                                                             │
         │  REST (file only)                                           │
         │  ──── POST /data/export ─────────────────────────────────► │
         │  ◄──── CSV stream ───────────────────────────────────────── │
         │  ──── POST /data/import (multipart) ────────────────────► │
         └─────────────────────────────────────────────────────────────┘
```

---

## Architecture

### Layered Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                       TRANSPORT LAYER                             │
│  ┌─────────────────────┐  ┌────────────────────────────────┐     │
│  │  GraphQL (POST/WS)  │  │  REST (health + file I/O)      │     │
│  └─────────┬───────────┘  └────────┬───────────────────────┘     │
├────────────┼────────────────────────┼────────────────────────────┤
│            ▼                        ▼                             │
│  ┌─────────────────────────────────────────────┐                  │
│  │           GRAPHQL ENGINE                     │                  │
│  │  graphql-kotlin-ktor-server                  │                  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────────┐ │                  │
│  │  │  Schema  │ │Resolvers│ │DataLoaders   │ │                  │
│  │  │ (SDL)    │ │(fetchers)│ │(batch, N+1)  │ │                  │
│  │  └──────────┘ └────┬─────┘ └──────────────┘ │                  │
│  └────────────────────┼────────────────────────┘                  │
├───────────────────────┼──────────────────────────────────────────┤
│                       ▼                                           │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                   SERVICE LAYER                              │  │
│  │  ┌────────────┐ ┌────────────┐ ┌──────────┐ ┌──────────┐  │  │
│  │  │ExpenseSvc  │ │BudgetSvc   │ │CategorySvc││ReportSvc │  │  │
│  │  └────────────┘ └────────────┘ └──────────┘ └──────────┘  │  │
│  └───────────────────────┬────────────────────────────────────┘  │
├──────────────────────────┼───────────────────────────────────────┤
│                          ▼                                        │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                REPOSITORY LAYER                              │  │
│  │  ┌────────────┐ ┌────────────┐ ┌──────────┐ ┌──────────┐  │  │
│  │  │ExpenseRepo │ │BudgetRepo   │ │CategoryR.│ │SyncRepo  │  │  │
│  │  │interface   │ │interface    │ │interface │ │interface  │  │  │
│  │  └─────┬──────┘ └─────┬──────┘ └────┬─────┘ └────┬─────┘  │  │
│  │        ▼              ▼             ▼           ▼           │  │
│  │  ┌──────────────────────────────────────────────────┐      │  │
│  │  │        ExposedRepositoryImpl (concrete)          │      │  │
│  │  └──────────────────────┬───────────────────────────┘      │  │
│  └─────────────────────────┼──────────────────────────────────┘  │
├───────────────────────────┼────────────────────────────────────┤
│                           ▼                                      │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                   DATABASE LAYER                             │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │  │
│  │  │  Exposed ORM  │  │  HikariCP CP │  │  Flyway Migrate  │  │  │
│  │  └──────┬───────┘  └──────┬───────┘  └────────┬─────────┘  │  │
│  │         │                 │                    │            │  │
│  │         ▼                 ▼                    ▼            │  │
│  │  ┌────────────────────────────────────────────────────┐    │  │
│  │  │            H2 (dev) / PostgreSQL (prod)             │    │  │
│  │  └────────────────────────────────────────────────────┘    │  │
│  └────────────────────────────────────────────────────────────┘  │
├──────────────────────────────────────────────────────────────────┤
│                        CROSS-CUTTING                              │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────────────────┐  │
│  │  Koin DI │ │StatusPg.│ │  CORS    │ │  Logging (Logback)  │  │
│  └──────────┘ └──────────┘ └──────────┘ └────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
```

### Data Flow (Request Lifecycle)

```
1. HTTP Request (POST /graphql)
2. Ktor Routing → GraphQL plugin parses query
3. graphql-kotlin validates query against schema (type-checking + depth/complexity limits)
4. Resolver functions invoked with arguments
5. DataLoaders batch-load related entities (prevents N+1)
6. Service layer executes business logic (validation, authorization, calculations)
7. Repository layer runs Exposed queries against database
8. Response assembled → serialized → returned as JSON
```

### SOLID Principles Applied

| Principle | Application |
|-----------|-------------|
| **S**ingle Responsibility | Each resolver handles exactly one domain query. Each service handles one business capability. Each repository interface handles one entity type. |
| **O**pen/Closed | New GraphQL queries add new resolver functions without modifying existing ones. New service capabilities extend via new interfaces/implementations. |
| **L**iskov Substitution | Repository interfaces have both `ExposedRepositoryImpl` (production) and `InMemoryRepository` (tests). Services depend on interfaces, so either can be swapped. |
| **I**nterface Segregation | Separate interfaces per entity: `ExpenseRepository`, `BudgetRepository`, `CategoryRepository` — no monolithic "DataAccess" interface. |
| **D**ependency Inversion | Resolvers receive service interfaces via constructor injection (Koin). Services receive repository interfaces. High-level modules never depend on low-level details. |

---

## GraphQL Schema

### Overview

- **Endpoint**: `POST /graphql` (and `GET /graphql` for IDE/SDL introspection)
- **Subscriptions**: WebSocket at `/graphql/ws`
- **GraphQL IDE (dev)**: `GET /graphiql` when running locally
- **Schema generation**: Code-first via graphql-kotlin or SDL-first with `.graphql` files

### Root Types

```graphql
type Query {
    # Expenses
    expenses(
        filter: ExpenseFilter,
        pagination: PaginationInput,
        sort: SortInput
    ): ExpenseConnection!
    expense(id: ID!): Expense

    # Categories
    categories: [Category!]!
    category(id: ID!): Category

    # Budgets
    budgets(period: Period): [Budget!]!
    budget(id: ID!): Budget

    # Reports
    reportSummary(period: Period, year: Int, month: Int): ReportSummary!
    reportMonthly(year: Int!, month: Int!): MonthlyReport!
    reportYearly(year: Int!): YearlyReport!

    # Dashboard (aggregated home screen data)
    dashboard: Dashboard!
}

type Mutation {
    # Expenses
    createExpense(input: CreateExpenseInput!): Expense!
    updateExpense(id: ID!, input: UpdateExpenseInput!): Expense!
    deleteExpense(id: ID!): DeleteResult!

    # Categories
    createCategory(input: CreateCategoryInput!): Category!
    updateCategory(id: ID!, input: UpdateCategoryInput!): Category!
    deleteCategory(id: ID!): DeleteResult!

    # Budgets
    createBudget(input: CreateBudgetInput!): Budget!
    updateBudget(id: ID!, input: UpdateBudgetInput!): Budget!
    deleteBudget(id: ID!): DeleteResult!

    # Offline Sync
    syncBatch(inputs: [SyncInput!]!): SyncResult!
}

type Subscription {
    budgetAlert(threshold: Float): BudgetAlert!
    expenseUpdated: Expense!
}
```

### Type Definitions

```graphql
# ──── Pagination ────

type PageInfo {
    hasNextPage: Boolean!
    hasPreviousPage: Boolean!
    startCursor: String
    endCursor: String
}

input PaginationInput {
    first: Int!   = 20
    after: String
    last: Int
    before: String
}

input SortInput {
    field: SortField!
    order: SortOrder!
}

enum SortField { DATE, AMOUNT, TITLE }
enum SortOrder { ASC, DESC }

# ──── Expense ────

type ExpenseConnection {
    edges: [ExpenseEdge!]!
    pageInfo: PageInfo!
    totalCount: Int!
    totalAmount: Float!
}

type ExpenseEdge {
    node: Expense!
    cursor: String!
}

type Expense {
    id: ID!
    title: String!
    amount: Float!
    category: Category!
    date: String!                   # ISO-8601 date: "2026-05-25"
    createdAt: String!              # ISO-8601 datetime
    updatedAt: String!
}

input ExpenseFilter {
    search: String                  # Full-text search on title
    categoryId: ID
    dateFrom: String                # "2026-01-01"
    dateTo: String
    minAmount: Float
    maxAmount: Float
}

input CreateExpenseInput {
    title: String!
    amount: Float!
    categoryId: ID!
    date: String!
}

input UpdateExpenseInput {
    title: String
    amount: Float
    categoryId: ID
    date: String
}

# ──── Category ────

type Category {
    id: ID!
    name: String!
    color: String!                  # Hex color: "#FF5252"
    icon: String!                   # Emoji or icon name: "FOOD"
    isDefault: Boolean!             # System-defined vs user-created
    expenseCount: Int!              # Computed: number of expenses in this category
    totalAmount: Float!             # Computed: sum of expenses in current period
}

input CreateCategoryInput {
    name: String!
    color: String!
    icon: String
}

input UpdateCategoryInput {
    name: String
    color: String
    icon: String
}

# ──── Budget ────

type Budget {
    id: ID!
    category: Category!
    limit: Float!
    period: Period!
    spent: Float!                   # Computed: current spending
    percentage: Float!              # Computed: spent / limit * 100
    daysRemaining: Int!             # Computed: days left in period
    isOverLimit: Boolean!           # Computed: percentage >= 100
    isWarning: Boolean!             # Computed: percentage >= 90
}

enum Period { WEEKLY, MONTHLY, YEARLY }

input CreateBudgetInput {
    categoryId: ID!
    limit: Float!
    period: Period!
}

input UpdateBudgetInput {
    limit: Float
    period: Period
}

# ──── Reports ────

type ReportSummary {
    totalSpent: Float!
    totalIncome: Float!                 # Future use
    netAmount: Float!                   # Future use
    averageDaily: Float!
    categoryBreakdown: [CategoryBreakdown!]!
    dailyTrend: [DailyDataPoint!]!
    topExpenses: [Expense!]!
    previousPeriodComparison: Comparison!
}

type CategoryBreakdown {
    category: Category!
    amount: Float!
    percentage: Float!
    transactionCount: Int!
}

type DailyDataPoint {
    date: String!
    amount: Float!
    transactionCount: Int!
}

type MonthlyReport {
    year: Int!
    month: Int!
    totalSpent: Float!
    days: [DailyDataPoint!]!
    categoryBreakdown: [CategoryBreakdown!]!
    topExpenses: [Expense!]!
}

type YearlyReport {
    year: Int!
    totalSpent: Float!
    monthlyBreakdown: [MonthlySummary!]!
    categoryBreakdown: [CategoryBreakdown!]!
    comparison: Comparison!
}

type MonthlySummary {
    month: Int!
    totalSpent: Float!
    transactionCount: Int!
}

type Comparison {
    amount: Float!
    percentage: Float!
    trend: TrendDirection!
}

enum TrendDirection { UP, DOWN, SAME }

# ──── Dashboard (aggregated home data) ────

type Dashboard {
    totalSpent: Float!
    previousPeriodComparison: Comparison!
    recentTransactions: [Expense!]!     # Last 5
    budgetOverview: [Budget!]!          # Active budgets with computed progress
}

# ──── Sync (offline-first) ────

input SyncInput {
    clientId: String!                   # Unique device/client ID
    lastSyncedAt: String!               # Client's last sync timestamp
    operations: [SyncOperation!]!
}

input SyncOperation {
    operationType: SyncOperationType!
    entityType: SyncEntityType!
    localId: String!                    # Client-generated UUID
    serverId: ID                        # Null for creates
    data: String!                       # JSON-encoded entity data
    timestamp: String!
    conflictVersion: Int!               # Optimistic locking version
}

enum SyncOperationType { CREATE, UPDATE, DELETE }
enum SyncEntityType { EXPENSE, BUDGET, CATEGORY }

type SyncResult {
    success: Boolean!
    applied: [SyncApplied!]!
    conflicts: [SyncConflict!]!
    serverTimestamp: String!
}

type SyncApplied {
    operationId: String!
    serverId: ID!
    entity: SyncEntityType!
}

type SyncConflict {
    operationId: String!
    serverId: ID!
    entity: SyncEntityType!
    serverVersion: Int!
    serverData: String!                 # JSON-encoded current server entity
    resolution: ConflictResolution!
}

enum ConflictResolution { CLIENT_WINS, SERVER_WINS, MANUAL }

# ──── Shared Types ────

type DeleteResult {
    success: Boolean!
    id: ID!
}

type BudgetAlert {
    budget: Budget!
    message: String!
    severity: AlertSeverity!
}

enum AlertSeverity { WARNING, CRITICAL }

# ──── Errors ────

interface ApiError {
    code: String!
    message: String!
}

type ValidationError implements ApiError {
    code: String!
    message: String!
    field: String!
}

type NotFoundError implements ApiError {
    code: String!
    message: String!
    resourceType: String!
    resourceId: ID!
}
```

### Example Queries & Mutations

#### Home Dashboard (1 round-trip vs 3 REST calls)

```graphql
query HomeDashboard {
    dashboard {
        totalSpent
        previousPeriodComparison {
            amount
            percentage
            trend
        }
        recentTransactions {
            id
            title
            amount
            category { name color icon }
            date
        }
        budgetOverview {
            category { name color }
            limit
            spent
            percentage
            daysRemaining
            isWarning
            isOverLimit
        }
    }
}
```

#### Paginated Expenses with Filters

```graphql
query Expenses($filter: ExpenseFilter, $pagination: PaginationInput, $sort: SortInput) {
    expenses(filter: $filter, pagination: $pagination, sort: $sort) {
        edges {
            node {
                id
                title
                amount
                category { name color icon }
                date
            }
            cursor
        }
        pageInfo { hasNextPage endCursor }
        totalCount
        totalAmount
    }
}
```

#### Batch Sync (offline client)

```graphql
mutation BatchSync($input: SyncInput!) {
    syncBatch(inputs: [$input]) {
        success
        applied { operationId serverId entity }
        conflicts {
            operationId
            serverId
            serverData
            serverVersion
            resolution
        }
        serverTimestamp
    }
}
```

#### Budget Alert Subscription

```graphql
subscription WatchBudgetAlerts {
    budgetAlert(threshold: 90) {
        budget {
            category { name color }
            limit
            spent
            percentage
        }
        message
        severity
    }
}
```

---

## REST Endpoints

Minimal REST surface — only for operations unsuited to GraphQL:

| Method | Path | Purpose | Request | Response |
|--------|------|---------|---------|----------|
| `GET` | `/health` | Health check | — | `{ "status": "UP", "timestamp": "...", "version": "1.0.0" }` |
| `POST` | `/data/export` | Export CSV file | `Content-Type: application/json` `{ "dateFrom": "...", "dateTo": "..." }` | `Content-Type: text/csv` stream |
| `POST` | `/data/import` | Import CSV file | `Content-Type: multipart/form-data` (CSV file) | `{ "imported": 42, "skipped": 2, "errors": [] }` |

---

## Data Models

### Database Tables

#### `expenses`

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGINT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Server-generated |
| `user_id` | `BIGINT` | `FOREIGN KEY -> users(id)`, `NOT NULL` | Future auth |
| `title` | `VARCHAR(255)` | `NOT NULL` | |
| `amount` | `DOUBLE` | `NOT NULL` | Always positive (debits only for v1) |
| `category_id` | `BIGINT` | `FOREIGN KEY -> categories(id)`, `NOT NULL` | |
| `date` | `DATE` | `NOT NULL` | User-specified date (may differ from created_at) |
| `description` | `TEXT` | `NULL` | Optional detailed description |
| `receipt_url` | `VARCHAR(500)` | `NULL` | Future: S3/cloud storage URL |
| `location_lat` | `DOUBLE` | `NULL` | Future: GPS tagging |
| `location_lng` | `DOUBLE` | `NULL` | Future: GPS tagging |
| `conflict_version` | `INT` | `NOT NULL DEFAULT 1` | Optimistic locking for offline sync |
| `client_id` | `VARCHAR(100)` | `NULL` | Client-generated UUID for dedup |
| `is_deleted` | `BOOLEAN` | `NOT NULL DEFAULT false` | Soft delete |
| `created_at` | `TIMESTAMP` | `NOT NULL DEFAULT NOW()` | |
| `updated_at` | `TIMESTAMP` | `NOT NULL DEFAULT NOW()` | |

#### `categories`

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGINT` | `PRIMARY KEY`, `AUTO_INCREMENT` | |
| `user_id` | `BIGINT` | `FOREIGN KEY -> users(id)`, `NULL` | `NULL` for system defaults |
| `name` | `VARCHAR(100)` | `NOT NULL` | |
| `color` | `VARCHAR(9)` | `NOT NULL` | Hex with `#`: `"#FF5252"` |
| `icon` | `VARCHAR(50)` | `NOT NULL` | Emoji or icon key: `"FOOD"` |
| `is_default` | `BOOLEAN` | `NOT NULL DEFAULT false` | System-defined (non-deletable) |
| `sort_order` | `INT` | `NOT NULL DEFAULT 0` | Display order |
| `created_at` | `TIMESTAMP` | `NOT NULL DEFAULT NOW()` | |

**Seed data (default categories):**

| Name | Color | Icon | is_default |
|------|-------|------|------------|
| Food | `#FF5252` | `FOOD` | `true` |
| Transport | `#448AFF` | `TRANSPORT` | `true` |
| Shopping | `#FFAB40` | `SHOPPING` | `true` |
| Bills | `#7C4DFF` | `BILLS` | `true` |
| Entertainment | `#E040FB` | `ENTERTAINMENT` | `true` |
| Other | `#9E9E9E` | `OTHER` | `true` |

#### `budgets`

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGINT` | `PRIMARY KEY`, `AUTO_INCREMENT` | |
| `user_id` | `BIGINT` | `FOREIGN KEY -> users(id)`, `NOT NULL` | |
| `category_id` | `BIGINT` | `FOREIGN KEY -> categories(id)`, `NOT NULL` | |
| `limit` | `DOUBLE` | `NOT NULL` | Budget cap |
| `period` | `VARCHAR(10)` | `NOT NULL` | `"WEEKLY"`, `"MONTHLY"`, `"YEARLY"` |
| `start_date` | `DATE` | `NOT NULL` | Budget period start |
| `created_at` | `TIMESTAMP` | `NOT NULL DEFAULT NOW()` | |
| `updated_at` | `TIMESTAMP` | `NOT NULL DEFAULT NOW()` | |

#### `users` (future)

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGINT` | `PRIMARY KEY`, `AUTO_INCREMENT` | |
| `email` | `VARCHAR(255)` | `UNIQUE`, `NOT NULL` | |
| `password_hash` | `VARCHAR(255)` | `NOT NULL` | bcrypt |
| `display_name` | `VARCHAR(100)` | `NOT NULL` | |
| `preferences` | `JSONB` | `NULL` | Currency, theme, etc. |
| `created_at` | `TIMESTAMP` | `NOT NULL DEFAULT NOW()` | |
| `updated_at` | `TIMESTAMP` | `NOT NULL DEFAULT NOW()` | |

#### `sync_log`

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGINT` | `PRIMARY KEY`, `AUTO_INCREMENT` | |
| `user_id` | `BIGINT` | `FOREIGN KEY -> users(id)`, `NOT NULL` | |
| `client_id` | `VARCHAR(100)` | `NOT NULL` | Device identifier |
| `operation_type` | `VARCHAR(10)` | `NOT NULL` | `"CREATE"`, `"UPDATE"`, `"DELETE"` |
| `entity_type` | `VARCHAR(20)` | `NOT NULL` | `"EXPENSE"`, `"BUDGET"`, `"CATEGORY"` |
| `entity_id` | `BIGINT` | `NULL` | Server entity ID after operation |
| `client_entity_id` | `VARCHAR(100)` | `NOT NULL` | Client-generated UUID |
| `payload` | `JSONB` | `NOT NULL` | Full entity data at time of sync |
| `conflict_occurred` | `BOOLEAN` | `NOT NULL DEFAULT false` | |
| `resolution` | `VARCHAR(10)` | `NULL` | `"CLIENT_WINS"`, `"SERVER_WINS"` |
| `created_at` | `TIMESTAMP` | `NOT NULL DEFAULT NOW()` | |

### Entity Relationships

```
users 1───* expenses        A user has many expenses
users 1───* budgets         A user has many budgets
users 1───* categories      A user has many categories (plus defaults)
users 1───* sync_logs       A user has many sync log entries
categories 1───* expenses   An expense belongs to one category
categories 1───* budgets    A budget targets one category (unique per period)
```

### Exposed Table Definitions

```kotlin
// Tables.kt
object ExpensesTable : Table("expenses") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UsersTable.id)
    val title = varchar("title", 255)
    val amount = double("amount")
    val categoryId = long("category_id").references(CategoriesTable.id)
    val date = date("date")
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

object CategoriesTable : Table("categories") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").nullable().references(UsersTable.id)
    val name = varchar("name", 100)
    val color = varchar("color", 9)
    val icon = varchar("icon", 50)
    val isDefault = bool("is_default").default(false)
    val sortOrder = integer("sort_order").default(0)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}

object BudgetsTable : Table("budgets") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UsersTable.id)
    val categoryId = long("category_id").references(CategoriesTable.id)
    val limit = double("limit")
    val period = varchar("period", 10)    // WEEKLY | MONTHLY | YEARLY
    val startDate = date("start_date")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}
```

---

## Tech Stack

| Category | Library | Version | Purpose |
|----------|---------|---------|---------|
| **Framework** | Ktor Server (Netty) | 2.3.12 | HTTP server engine |
| **GraphQL** | graphql-kotlin-ktor-server | 8.x | GraphQL schema + execution |
| **ORM** | Exposed (Core + DAO + JDBC) | 0.53.0 | Type-safe SQL queries |
| **DI** | Koin (core + ktor + logger) | 3.5.6 | Dependency injection |
| **Serialization** | kotlinx-serialization-json | 1.7.3 | JSON for REST + internal |
| **Database (dev)** | H2 | 2.2.224 | In-memory local dev |
| **Database (prod)** | PostgreSQL | 42.7.4 | Production persistence |
| **Connection Pool** | HikariCP | (bundled with Exposed) | Pooled DB connections |
| **Migrations** | Flyway | 10.x (recommended) | Schema versioning |
| **Logging** | Logback | 1.5.13 | Structured/console logging |
| **Testing** | ktor-server-test-host | 2.3.12 | Ktor test engine |

### Proposed Dependencies (add to `build.gradle.kts`)

```kotlin
dependencies {
    // Ktor
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.websockets)          // For GraphQL subscriptions

    // GraphQL
    implementation("com.expediagroup:graphql-kotlin-ktor-server:8.0.0")

    // Koin DI
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger)

    // Exposed ORM
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.json)                    // JSONB support

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Databases
    implementation(libs.h2.database)
    implementation(libs.postgresql.jdbc)
    implementation("com.zaxxer:HikariCP:5.1.0")          // Connection pooling

    // Migrations
    implementation("org.flywaydb:flyway-core:10.22.0")
    implementation("org.flywaydb:flyway-database-postgresql:10.22.0")

    // Logging
    implementation(libs.logback.classic)
    implementation(libs.kotlinx.coroutines.core)

    // Test
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit)
    testImplementation("com.expediagroup:graphql-kotlin-ktor-server-testing:8.0.0")
    testImplementation(libs.kotlinx.coroutines.test)
}
```

---

## Project Structure

```
backend/src/
├── main/
│   ├── kotlin/dev/oruizp/expensetracker/backend/
│   │   ├── Application.kt                   # Entry point (Netty, port 8080)
│   │   ├── di/
│   │   │   └── Modules.kt                   # Koin DI module wiring
│   │   ├── plugins/
│   │   │   ├── GraphQLPlugin.kt             # graphql-kotlin config
│   │   │   ├── StatusPagesPlugin.kt         # Error handling
│   │   │   ├── CORSPPlugin.kt               # CORS config
│   │   │   └── MonitoringPlugin.kt          # Call logging, metrics
│   │   ├── routes/
│   │   │   ├── HealthRoutes.kt              # GET /health
│   │   │   └── DataRoutes.kt                # POST /data/export, /data/import
│   │   ├── graphql/
│   │   │   ├── schema/
│   │   │   │   └── schema.graphql           # SDL schema definition (optional)
│   │   │   ├── resolvers/
│   │   │   │   ├── ExpenseResolver.kt       # Query.expenses, Query.expense
│   │   │   │   ├── ExpenseMutationResolver.kt # Mutation.create/update/delete expense
│   │   │   │   ├── CategoryResolver.kt
│   │   │   │   ├── BudgetResolver.kt
│   │   │   │   ├── BudgetMutationResolver.kt
│   │   │   │   ├── ReportResolver.kt
│   │   │   │   ├── DashboardResolver.kt
│   │   │   │   └── SyncResolver.kt
│   │   │   ├── subscriptions/
│   │   │   │   └── BudgetSubscription.kt
│   │   │   ├── dataloaders/
│   │   │   │   └── CategoryDataLoader.kt    # Batch loads categories for expenses
│   │   │   └── scalars/
│   │   │       └── DateScalar.kt            # Custom date scalar
│   │   ├── services/
│   │   │   ├── ExpenseService.kt            # Business logic: validation, calc
│   │   │   ├── BudgetService.kt             # Budget progress calculation
│   │   │   ├── CategoryService.kt
│   │   │   ├── ReportService.kt             # Aggregation queries
│   │   │   └── SyncService.kt               # Conflict resolution, batch ops
│   │   ├── repositories/
│   │   │   ├── ExpenseRepository.kt         # Interface
│   │   │   ├── ExpenseRepositoryImpl.kt     # Exposed implementation
│   │   │   ├── BudgetRepository.kt
│   │   │   ├── BudgetRepositoryImpl.kt
│   │   │   ├── CategoryRepository.kt
│   │   │   ├── CategoryRepositoryImpl.kt
│   │   │   ├── SyncRepository.kt
│   │   │   └── SyncRepositoryImpl.kt
│   │   ├── models/
│   │   │   ├── Expense.kt                   # @Serializable DTO
│   │   │   ├── Category.kt
│   │   │   ├── Budget.kt
│   │   │   ├── Report.kt
│   │   │   ├── Dashboard.kt
│   │   │   ├── Sync.kt
│   │   │   └── ApiError.kt                 # Error response types
│   │   ├── database/
│   │   │   ├── DatabaseFactory.kt           # HikariCP + DB connection setup
│   │   │   ├── Tables.kt                    # Exposed table definitions
│   │   │   ├── Migrations.kt                # Flyway integration
│   │   │   └── SeedData.kt                  # Default categories seed
│   │   └── config/
│   │       └── AppConfig.kt                 # Type-safe config from environment
│   └── resources/
│       ├── application.conf                 # HOCON config (optional)
│       └── logback.xml                      # Logging config
└── test/
    └── kotlin/dev/oruizp/expensetracker/backend/
        ├── graphql/
        │   ├── ExpenseResolverTest.kt
        │   ├── BudgetResolverTest.kt
        │   └── ReportResolverTest.kt
        ├── services/
        │   ├── ExpenseServiceTest.kt
        │   ├── BudgetServiceTest.kt
        │   └── SyncServiceTest.kt
        ├── repositories/
        │   ├── ExpenseRepositoryTest.kt
        │   └── BudgetRepositoryTest.kt
        ├── routes/
        │   └── HealthRouteTest.kt
        ├── integration/
        │   └── GraphQLIntegrationTest.kt
        └── testutil/
            ├── TestDatabase.kt              # H2 in-memory test DB factory
            └── MockFactories.kt             # Test fixture builders
```

---

## Error Handling

### Strategy

- **Service layer**: returns `Result<T, AppError>` — no exceptions for business logic failures
- **Repository layer**: throws on DB failures → caught by service layer and wrapped
- **GraphQL layer**: `graphql-kotlin` serializes errors into `errors[]` array with `extensions` object
- **Ktor layer**: `StatusPages` plugin catches unhandled exceptions and returns proper HTTP codes

### Error Response Format

```json
// GraphQL error
{
    "errors": [
        {
            "message": "Validation failed",
            "locations": [{"line": 2, "column": 3}],
            "path": ["createExpense"],
            "extensions": {
                "code": "VALIDATION_ERROR",
                "fields": [
                    {"field": "title", "message": "Title cannot be empty"},
                    {"field": "amount", "message": "Amount must be greater than 0"}
                ]
            }
        }
    ],
    "data": null
}
```

### HTTP Status Codes (REST)

| Code | Scenario |
|------|----------|
| `200` | Success |
| `201` | Resource created |
| `400` | Validation error (bad request) |
| `401` | Authentication required |
| `403` | Forbidden (wrong user) |
| `404` | Resource not found |
| `409` | Conflict (sync version mismatch) |
| `413` | Payload too large (CSV import) |
| `422` | Unprocessable entity (CSV parse error) |
| `429` | Rate limit exceeded |
| `500` | Internal server error |

### Error Codes (extensions.code)

| Code | Description |
|------|-------------|
| `VALIDATION_ERROR` | Input validation failed |
| `NOT_FOUND` | Requested resource does not exist |
| `CONFLICT` | Optimistic locking version mismatch |
| `UNAUTHORIZED` | Missing or invalid authentication |
| `FORBIDDEN` | Authenticated but not authorized |
| `RATE_LIMITED` | Too many requests |
| `INTERNAL_ERROR` | Unexpected server error |
| `DEPENDENCY_ERROR` | External service failure |
| `SYNC_CONFLICT` | Offline sync conflict (returned in SyncResult) |

### StatusPages Config

```kotlin
fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<NotFoundException> { call, cause ->
            call.respond(status = HttpStatusCode.NotFound, mapOf(
                "code" to "NOT_FOUND",
                "message" to cause.message ?: "Resource not found"
            ))
        }
        exception<ValidationException> { call, cause ->
            call.respond(status = HttpStatusCode.BadRequest, mapOf(
                "code" to "VALIDATION_ERROR",
                "message" to cause.message,
                "fields" to cause.errors.map { it.toMap() }
            ))
        }
        exception<Throwable> { call, cause ->
            log.error(cause) { "Unhandled exception" }
            call.respond(status = HttpStatusCode.InternalServerError, mapOf(
                "code" to "INTERNAL_ERROR",
                "message" to "An unexpected error occurred"
            ))
        }
    }
}
```

---

## Security

### Current Layer

| Measure | Implementation |
|---------|---------------|
| **CORS** | `install(CORS) { allowHost("localhost:8081"); allowHost("*.yourdomain.com") }` |
| **Query depth limiting** | `graphql-kotlin` `maxDepth = 10` |
| **Query complexity limiting** | `graphql-kotlin` `maxComplexity = 200` |
| **Payload size limiting** | Ktor `maxSize` for request body |
| **Rate limiting** | Custom plugin or Ktor throttling |

### Future (Phase 9+)

| Measure | Implementation |
|---------|---------------|
| **Auth** | JWT (access + refresh tokens), Ktor `Authentication` plugin with `JWT` provider |
| **Password hashing** | bcrypt (`Bcrypt.verify()`) |
| **Persisted queries** | Apollo persisted queries whitelist (prevents arbitrary queries) |
| **Request validation** | Strict input validation on all mutations |
| **HTTPS** | Ktor SSL support or reverse proxy (Nginx/Caddy) |
| **Secrets management** | Environment variables via `AppConfig.kt`, never hardcoded |

---

## Database

### Configuration

```kotlin
// AppConfig.kt — type-safe config from environment
data class DatabaseConfig(
    val driver: String = getEnv("DB_DRIVER", "org.h2.Driver"),
    val url: String = getEnv("DB_URL", "jdbc:h2:mem:expense_tracker"),
    val user: String = getEnv("DB_USER", "sa"),
    val password: String = getEnv("DB_PASSWORD", ""),
    val poolSize: Int = getEnv("DB_POOL_SIZE", "10").toInt(),
    val migrationsEnabled: Boolean = getEnv("DB_MIGRATIONS_ENABLED", "true").toBoolean(),
)
```

### Environments

| Environment | Driver | URL | Pool Size |
|-------------|--------|-----|-----------|
| **Development** | `org.h2.Driver` | `jdbc:h2:file:./data/expense_tracker` | 5 |
| **Test** | `org.h2.Driver` | `jdbc:h2:mem:test` | 1 |
| **Production** | `org.postgresql.Driver` | `jdbc:postgresql://localhost:5432/expense_tracker` | 20 |

### Connection Pool (HikariCP)

```kotlin
fun init(config: DatabaseConfig) {
    val dataSource = HikariDataSource(HikariConfig().apply {
        driverClassName = config.driver
        jdbcUrl = config.url
        username = config.user
        password = config.password
        maximumPoolSize = config.poolSize
        minimumIdle = 3
        idleTimeout = 30_000
        connectionTimeout = 10_000
        maxLifetime = 600_000
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    })
    Database.connect(dataSource)
}
```

### Migrations (Flyway)

```
backend/src/main/resources/db/migration/
├── V1__create_users_table.sql
├── V2__create_categories_table.sql
├── V3__seed_default_categories.sql
├── V4__create_expenses_table.sql
├── V5__create_budgets_table.sql
├── V6__create_sync_log_table.sql
├── V7__add_user_preferences.sql
└── V8__add_receipt_location_fields.sql
```

### Exposed Transactions

```kotlin
class ExpenseRepositoryImpl(private val db: Database) : ExpenseRepository {
    override suspend fun create(input: CreateExpenseInput): Expense = dbQuery {
        ExpensesTable.insert {
            it[title] = input.title
            it[amount] = input.amount
            it[categoryId] = input.categoryId.toLong()
            it[date] = LocalDate.parse(input.date)
            it[clientId] = input.clientId
        }.resultedValues?.single()?.toExpense()
            ?: throw DatabaseException("Insert failed")
    }

    override suspend fun findByFilter(
        filter: ExpenseFilter,
        pagination: PaginationInput,
        sort: SortInput
    ): PaginatedResult<Expense> = dbQuery {
        val query = ExpensesTable
            .selectAll()
            .apply { filter.applyTo(this) }
            .apply { sort.applyTo(this) }
        val totalCount = query.count()
        val totalAmount = query.map { it[ExpensesTable.amount] }.sum()
        val items = query
            .limit(pagination.first)
            .offset(pagination.offset)
            .map { it.toExpense() }
        PaginatedResult(items, totalCount, totalAmount)
    }
}
```

---

## Build & Run

### Local Development

```bash
# Build + test
./gradlew :backend:build :backend:test

# Run development server (H2 in-memory, port 8080)
./gradlew :backend:run

# Run with PostgreSQL (env overrides)
DB_DRIVER=org.postgresql.Driver \
DB_URL=jdbc:postgresql://localhost:5432/expense_tracker \
DB_USER=postgres \
DB_PASSWORD=postgres \
./gradlew :backend:run

# Run a single test class
./gradlew :backend:test --tests "dev.oruizp.expensetracker.backend.repositories.ExpenseRepositoryTest"

# Run a single test method
./gradlew :backend:test --tests "dev.oruizp.expensetracker.backend.routes.HealthRouteTest.health endpoint returns UP status"

# Generate GraphQL schema SDL file
./gradlew :backend:graphqlGenerateSDL
# Output: build/schema.graphql

# Run tests with coverage
./gradlew :backend:test jacocoTestReport
# Output: build/reports/jacoco/
```

### Docker (Local)

```bash
# Build Docker image
docker build -t expense-tracker-backend:latest -f Dockerfile .

# Run with H2 (in-memory)
docker run -p 8080:8080 expense-tracker-backend:latest

# Run with PostgreSQL
docker run -p 8080:8080 \
  -e DB_DRIVER=org.postgresql.Driver \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/expense_tracker \
  -e DB_USER=postgres \
  -e DB_PASSWORD=postgres \
  expense-tracker-backend:latest
```

### Docker Compose (Full Stack)

```yaml
# docker-compose.yml
version: "3.8"
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: expense_tracker
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    volumes:
      - pgdata:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 5s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      DB_DRIVER: org.postgresql.Driver
      DB_URL: jdbc:postgresql://postgres:5432/expense_tracker
      DB_USER: postgres
      DB_PASSWORD: postgres
      DB_POOL_SIZE: "10"
      SERVER_PORT: "8080"
      CORS_ORIGINS: "http://localhost:8081,http://localhost:3000,https://*.synology.me"
    depends_on:
      postgres:
        condition: service_healthy

volumes:
  pgdata:
```

```bash
# Start everything
docker compose up -d

# View logs
docker compose logs -f backend

# Stop
docker compose down

# Stop + delete volumes
docker compose down -v
```

---

## Testing Strategy

### Test Pyramid

```
         ╱╲
        ╱  ╲           E2E/Integration (GraphQL)
       ╱    ╲          ktor-test-host + TestApplication
      ╱──────╲
     ╱        ╲        Service tests
    ╱          ╲       JUnit 4 + MockK
   ╱────────────╲
  ╱              ╲     Repository tests
 ╱                ╲    Exposed + H2 in-memory
╱──────────────────╲
```

### Test Categories

| Layer | Test | Tool | Example |
|-------|------|------|---------|
| **Resolver** | GraphQL query/mutation execution | `graphql-kotlin-ktor-server-testing` | `testApplication { client.graphQL(query, variables) }` |
| **Service** | Business logic, validation | JUnit 4 + MockK | `expenseService.create(input)` with mocked repo |
| **Repository** | Exposed queries, transactions | Exposed + H2 in-memory | `expenseRepo.findByFilter(...)` with real DB |
| **Route** | REST endpoint response | `ktor-server-test-host` | `client.get("/health")` |
| **Integration** | Full GraphQL → DB flow | TestApplication + H2 | `mutation { createExpense }` → verify DB state |

### Test Patterns

```kotlin
// Repository test — real Exposed with H2
class ExpenseRepositoryTest {
    @Before
    fun setup() {
        Database.connect("jdbc:h2:mem:test", "org.h2.Driver")
        transaction { SchemaUtils.create(ExpensesTable, CategoriesTable) }
    }

    @Test
    fun `create expense persists to database`() = runTest {
        val repo = ExpenseRepositoryImpl()
        val expense = repo.create(validInput)
        assertEquals("Groceries", expense.title)
        assertNotNull(expense.id)
    }
}

// Service test — mocked repository
class ExpenseServiceTest {
    private val repo = mockk<ExpenseRepository>()
    private val service = ExpenseService(repo)

    @Test
    fun `create expense with empty title throws validation error`() = runTest {
        val result = service.create(input.copy(title = ""))
        assertTrue(result.isFailure)
        assertEquals("VALIDATION_ERROR", (result.exceptionOrNull() as ValidationError).code)
    }
}

// Resolver test — full GraphQL execution
class ExpenseResolverTest {
    @Test
    fun `query expenses returns paginated results`() = withTestApplication {
        application.module() // use production/DI wiring
        val response = client.graphQL("""
            query { expenses(pagination: {first: 10}) {
                edges { node { id title } }
                totalCount
            }}
        """)
        assertEquals(200, response.status.value)
        val data = response.bodyAsText()
        assertTrue(data.contains("totalCount"))
    }
}
```

---

---

## Deployment

### Synology NAS (via Docker)

This backend runs as a Docker container on Synology DSM using **Container Manager**.

#### Prerequisites

- Synology NAS with DSM 7.2+ and **Container Manager** installed
- Package Center → Container Manager → Install

#### Method 1: Manual Build & Deploy (recommended)

```bash
# 1. On your dev machine, build the Docker image (from project root)
docker build -t expense-tracker-backend:latest -f Dockerfile .

# 2. Save image to tarball
docker save expense-tracker-backend:latest | gzip > expense-tracker-backend.tar.gz

# 3. Copy to Synology via SCP
scp expense-tracker-backend.tar.gz youruser@your-nas.local:~/docker/

# 4. SSH into Synology, load the image
ssh youruser@your-nas.local
cd ~/docker
gunzip expense-tracker-backend.tar.gz
docker load -i expense-tracker-backend.tar

# 5. Run the container (replace paths/ports as needed)
docker run -d \
  --name expense-tracker-backend \
  -p 8080:8080 \
  -e DB_DRIVER=org.postgresql.Driver \
  -e DB_URL=jdbc:postgresql://192.168.1.100:5432/expense_tracker \
  -e DB_USER=postgres \
  -e DB_PASSWORD=your_secure_password \
  -e DB_POOL_SIZE=10 \
  -e SERVER_PORT=8080 \
  -e CORS_ORIGINS="https://your-nas.synology.me,http://localhost:8081" \
  --restart unless-stopped \
  expense-tracker-backend:latest

# View logs
docker logs -f expense-tracker-backend
```

#### Method 2: Container Manager UI

1. **Open Container Manager** → **Registry** → Search `expense-tracker-backend` (or use your private registry)
2. **Image** → Add from registry (or Load from file for a local tarball)
3. **Container** → Create → Select the image
4. Configure:

   | Setting | Value |
   |---------|-------|
   | **General** → Name | `expense-tracker-backend` |
   | **General** → Enable auto-restart | ✅ |
   | **Port Settings** → Local Port → 8080 → Container Port → 8080 | Map TCP |
   | **Environment** | Add variables from table below |
   | **Volume** | Mount `/app/data` for H2 file storage (optional) |

5. **Environment variables**:

   | Variable | Example Value | Notes |
   |----------|---------------|-------|
   | `DB_DRIVER` | `org.postgresql.Driver` or `org.h2.Driver` | For production use PostgreSQL |
   | `DB_URL` | `jdbc:postgresql://192.168.1.100:5432/expense_tracker` | Your NAS internal IP and DB name |
   | `DB_USER` | `postgres` | |
   | `DB_PASSWORD` | `your_secure_password` | |
   | `DB_POOL_SIZE` | `10` | Connection pool size |
   | `SERVER_PORT` | `8080` | Must match container port |
   | `CORS_ORIGINS` | `https://your-nas.synology.me` | Comma-separated allowed origins |

6. **Run PostgreSQL on Synology** (separate container or package):

   ```bash
   docker run -d \
     --name postgres \
     -p 5432:5432 \
     -e POSTGRES_DB=expense_tracker \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_PASSWORD=your_secure_password \
     -v /volume1/docker/postgres:/var/lib/postgresql/data \
     --restart unless-stopped \
     postgres:16-alpine
   ```

   Or install the **PostgreSQL** package from Synology Package Center (DSM 7.2+).

#### Reverse Proxy (Access via Domain)

In **DSM → Control Panel → Login Portal → Advanced → Reverse Proxy**:

| Setting | Value |
|---------|-------|
| **Source** → Protocol | `HTTPS` |
| **Source** → Hostname | `expenses.your-nas.synology.me` |
| **Source** → Port | `443` |
| **Enable HSTS** | ✅ |
| **Destination** → Protocol | `HTTP` |
| **Destination** → Hostname | `localhost` |
| **Destination** → Port | `8080` |

Now your backend is accessible at `https://expenses.your-nas.synology.me/graphql`.

#### Health Check

```bash
# From inside your network
curl http://your-nas.local:8080/health

# From outside (if reverse proxy is configured)
curl https://expenses.your-nas.synology.me/health

# Expected response:
# {"status":"UP","version":"1.0.0"}
```

---

### Scalability

| Concern | Solution |
|---------|----------|
| **Stateless design** | No server-side session state; all state in DB/client. Horizontally scalable by adding instances behind a load balancer. |
| **Read replicas** | Exposed supports multiple `Database` instances. Route reads to replicas, writes to primary. |
| **Caching** | GraphQL response caching via persisted queries + Redis for computed aggregations (dashboard, reports). Cache invalidation via mutation hooks. |
| **Connection pooling** | HikariCP tuned per-instance. Monitor with `HikariPoolMXBean`. |
| **Rate limiting** | Per-IP and per-token bucket rate limiter as Ktor plugin. |

### Federation (Microservices)

```
                  ┌─────────────┐
                  │  Apollo     │
                  │  Gateway    │
                  │  (Federated)│
                  └──────┬──────┘
         ┌───────────────┼───────────────┐
         │               │               │
         ▼               ▼               ▼
   ┌──────────┐   ┌──────────┐   ┌──────────┐
   │ Expense  │   │  Budget  │   │  Report  │
   │ Service  │   │  Service │   │  Service │
   │ (:8081)  │   │  (:8082) │   │  (:8083) │
   └──────────┘   └──────────┘   └──────────┘
```

When traffic grows, split monolith into federated subgraphs using `graphql-kotlin-federation`. Each subgraph owns its domain data and can be deployed independently.

### Feature Roadmap

| Feature | Backend Impact | Priority |
|---------|---------------|----------|
| **JWT Authentication** | New `users`, `auth` resolvers + Ktor auth plugin | High |
| **WebSocket subscriptions** | Budget alerts pushed in real-time | Medium |
| **Receipt image upload** | S3/MinIO storage + `Receipt` entity | Medium |
| **Location tagging** | GPS coordinates on expenses | Low |
| **Recurring expenses** | New `recurring_expenses` table + scheduler | Low |
| **Multi-currency** | Exchange rate API integration + `currency` field | Low |
| **Web client** | Same GraphQL schema, new Apollo Client target | Medium |
| **iOS client** | KMP shared GraphQL operations via Apollo iOS | Low |
| **Analytics webhook** | POST to external analytics on each mutation | Low |
| **Admin dashboard** | Admin-only GraphQL fields + RBAC | Low |

### API Versioning

GraphQL makes versioning largely unnecessary — fields can be deprecated via `@deprecated` directive while maintaining backward compatibility:

```graphql
type Expense {
    id: ID!
    title: String!
    amount: Float!
    category: Category!
    date: String!
    description: String @deprecated(reason: "Use 'notes' field instead")
    notes: String
}
```

### Monitoring

| Tool | Purpose |
|------|---------|
| **Ktor CallLogging** | Request/response logging |
| **Logback** | Structured JSON logging (production) |
| **Micrometer** | Metrics for Prometheus (future) |
| **Apollo Studio** | GraphQL schema registry + operation tracing |
| **OpenTelemetry** | Distributed tracing across microservices |
