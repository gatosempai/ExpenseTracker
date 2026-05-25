# Expense Tracker

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin)
![Compose](https://img.shields.io/badge/Compose-BOM_2024.09-4285F4?logo=jetpackcompose)
![Ktor](https://img.shields.io/badge/Backend-Ktor_2.3.12-007396?logo=ktor)
![License](https://img.shields.io/badge/License-MIT-yellow)

A full-stack expense tracking application with an Android client (Jetpack Compose) and a Ktor backend, sharing common business logic via KMP.

---

## Overview

ExpenseTracker helps users log, categorize, and analyze their daily spending. The Android app provides a rich Material 3 interface with offline-first Room storage, while the Ktor backend syncs data across devices and provides analytics endpoints. A KMP shared module (`:common`) enables code reuse between Android and potential iOS clients.

---

## Screenshots

| Home Dashboard | All Transactions | Budgets | Reports |
|:---:|:---:|:---:|:---:|
| _(TODO)_ | _(TODO)_ | _(TODO)_ | _(TODO)_ |

---

## Architecture

### Clean Architecture Layers

```
┌──────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                         │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  Compose UI (Screens / Components)                     │  │
│  │  ViewModels (StateFlow / State)                        │  │
│  └────────────────────────────────────────────────────────┘  │
├──────────────────────────────────────────────────────────────┤
│                      DOMAIN LAYER                             │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  Use Cases / Interactors                               │  │
│  │  Repository Interfaces                                 │  │
│  │  Domain Models                                         │  │
│  └────────────────────────────────────────────────────────┘  │
├──────────────────────────────────────────────────────────────┤
│                        DATA LAYER                             │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  Room DAOs (Local DB)                                  │  │
│  │  Retrofit API (Remote)                                 │  │
│  │  Repository Implementations                            │  │
│  │  DataStore (Preferences)                               │  │
│  └────────────────────────────────────────────────────────┘  │
├──────────────────────────────────────────────────────────────┤
│                        CORE LAYER                             │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  DI (Koin) / Extensions / Utilities / Common UI        │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

### Module Dependency Graph

```
                    ┌──────────────────────┐
                    │   :common (KMP)      │
                    └──────┬───────────────┘
                           │ expect/actual
          ┌────────────────┼────────────────┐
          │                │                │
          ▼                ▼                ▼
   ┌────────────┐  ┌──────────────┐  ┌──────────┐
   │ :android:  │  │ :android:    │  │ :backend │
   │   app      │  │   features/* │  │ (Ktor)   │
   └─────┬──────┘  └──────┬───────┘  └──────────┘
         │                │
         ▼                ▼
   ┌──────────────────────────────┐
   │  :android:core               │
   │  :android:data               │
   │  :android:domain             │
   └──────────────────────────────┘
```

### Data Flow

```
User Action → Composable → Intent/Event → ViewModel → Repository → [Room | API]
                                    │                          │
                                    ▼                          ▼
                              StateFlow ◄────── Flow ◄────────┘
                                    │
                                    ▼
                           Composable recomposes
```

---

## Tech Stack

### Android Client

| Category | Library | Version |
|----------|---------|---------|
| Language | Kotlin | 2.2.10 |
| UI | Jetpack Compose (BOM) | 2024.09.00 |
| UI | Material 3 | BOM |
| Navigation | Navigation Compose | 2.8.9 |
| Database | Room (KSP) | 2.7.0 |
| ViewModel | lifecycle-viewmodel-compose | 2.8.7 |
| DI | _(planned: Koin)_ | — |
| Networking | Retrofit + Moshi | 2.12.0 |
| Image Loading | Coil Compose | 2.7.0 |
| Preferences | DataStore Preferences | 1.1.7 |
| Location | Play Services Location | 21.3.0 |
| Camera | CameraX | 1.5.0 |
| Permissions | Accompanist Permissions | 0.37.3 |
| Build | AGP | 9.1.1 |
| Min SDK | — | 24 |
| Target SDK | — | 36 |

### Backend

| Category | Library | Version |
|----------|---------|---------|
| Framework | Ktor (Netty) | 2.3.12 |
| ORM | Exposed (Core/DAO/JDBC) | 0.53.0 |
| DI | Koin | 3.5.6 |
| Serialization | kotlinx-serialization | 1.7.3 |
| Database (dev) | H2 (in-memory) | 2.2.224 |
| Database (prod) | PostgreSQL | 42.7.4 |
| Logging | Logback | 1.5.13 |

### Shared (KMP)

| Category | Library | Version |
|----------|---------|---------|
| Platform | Kotlin Multiplatform | 2.2.10 |
| Coroutines | kotlinx-coroutines | 1.10.2 |

---

## Project Structure

```
ExpenseTracker/
├── android/
│   ├── app/                          # Main application module
│   │   └── src/main/java/dev/oruizp/expensetracker/
│   │       ├── MainActivity.kt       # Single-activity entry point
│   │       ├── data/                 # Room DB, DAO, Entities
│   │       │   ├── Expense.kt
│   │       │   ├── ExpenseDao.kt
│   │       │   └── ExpenseDatabase.kt
│   │       └── ui/
│   │           ├── home/             # Home screen + ViewModel
│   │           └── theme/            # Material 3 theme
│   ├── core/                         # Shared utilities, DI
│   ├── data/                         # Data layer (repositories)
│   ├── domain/                       # Domain layer (models, use cases)
│   └── features/
│       ├── transactions/             # Transaction list + detail
│       ├── budgets/                  # Budget management
│       ├── categories/               # Category management
│       ├── reports/                  # Charts & analytics
│       └── settings/                 # App preferences
├── backend/                          # Ktor server (port 8080)
│   └── src/main/kotlin/.../backend/
│       └── Application.kt           # Server entry point
├── common/                           # KMP shared module
├── gradle/
│   └── libs.versions.toml           # Version catalog
├── build.gradle.kts                  # Root build file
├── settings.gradle.kts               # Module includes
└── AGENTS.md                         # Dev agent instructions
```

---

## UI/UX Design Specification

### Design System

#### Color Palette

| Token | Light | Dark | Usage |
|-------|-------|------|-------|
| `Primary` | `#006D32` | `#7ED991` | Buttons, selected state, total card background |
| `OnPrimary` | `#FFFFFF` | `#003917` | Text/icons on primary |
| `PrimaryContainer` | `#99F6AB` | `#005224` | TopAppBar, chip selected |
| `OnPrimaryContainer` | `#00210B` | `#99F6AB` | Text on primary container |
| `Secondary` | `#4F6352` | _(default)_ | Toggle buttons |
| `SecondaryContainer` | `#D2E8D3` | _(default)_ | Chips, badges |
| `Tertiary` | `#3B6470` | _(default)_ | Accents |
| `Background` | `#FBFDF8` | `#191C19` | Page backgrounds |
| `Surface` | `#FBFDF8` | `#191C19` | Card & sheet backgrounds |
| `Error` | `#BA1A1A` | `#BA1A1A` | Critical warnings, destructive actions |
| `ErrorContainer` | `#FFDAD6` | `#FFDAD6` | Error banners |
| `OnSurfaceVariant` | — | — | Secondary text |

#### Category Colors

| Category | Color | Hex |
|----------|-------|-----|
| Food | Red | `#FF5252` |
| Transport | Blue | `#448AFF` |
| Shopping | Orange | `#FFAB40` |
| Bills | Purple | `#7C4DFF` |
| Entertainment | Pink | `#E040FB` |
| Other | Grey | `#9E9E9E` |

#### Typography

| Style | Size | Weight | Line Height | Usage |
|-------|------|--------|-------------|-------|
| `displayLarge` | 57sp | Bold | 64sp | Transaction detail amount |
| `displayMedium` | 45sp | Bold | 52sp | Dashboard total |
| `headlineSmall` | 24sp | Normal | 32sp | Sheet/modal titles |
| `titleLarge` | 22sp | Normal | 28sp | Section headers |
| `titleMedium` | 16sp | SemiBold | 24sp | Transaction/item title |
| `bodyLarge` | 16sp | Normal | 24sp | Descriptions, body text |
| `bodySmall` | 14sp | Normal | 20sp | Subtitles, dates, metadata |
| `labelSmall` | 11sp | Medium | 16sp | Chip labels, badges |

#### Spacing Grid

Base unit: `4dp`

| Token | Size | Usage |
|-------|------|-------|
| `spacing_xs` | 4dp | Icon margins, dot spacing |
| `spacing_sm` | 8dp | List item gaps, chip spacing |
| `spacing_md` | 12dp | In-card grouping |
| `spacing_lg` | 16dp | Screen padding, card padding |
| `spacing_xl` | 24dp | Section spacing |
| `spacing_xxl` | 32dp | Hero section margins |
| `spacing_3xl` | 48dp | Full-screen empty state top margin |

#### Shapes

| Element | Shape | Value |
|---------|-------|-------|
| Cards | RoundedCorner | 12dp |
| Total Spent Card | RoundedCorner | 24dp |
| Category icons | Circle | — |
| Filter chips | RoundedCorner | 20dp |
| Buttons | RoundedCorner | large (M3 default) |
| Bottom sheets | RoundedCorner (top) | 24dp |

### Navigation Architecture

```
Bottom Nav (5 tabs):
┌──────────┬────────────┬──────────┬──────────┬──────────┐
│   Home   │Transactions│ Budgets  │ Reports  │ Settings │
│  (home)  │  (txns)    │ (budgets)│ (reports)│(settings)│
└──────────┴────────────┴──────────┴──────────┴──────────┘
```

#### Route Table

| Route Pattern | Screen | Module | Notes |
|---------------|--------|--------|-------|
| `home` | Dashboard | `:features:home` | Entry point |
| `transactions` | Transaction list | `:features:transactions` | Filterable, searchable |
| `transactions/{id}` | Transaction detail | `:features:transactions` | View / edit / delete |
| `transactions/add` | Add transaction | `:features:transactions` | Opens inline or as dialog |
| `budgets` | Budget list | `:features:budgets` | Progress indicators |
| `budgets/add` | Add budget | `:features:budgets` | Per-category allocation |
| `budgets/{id}/edit` | Edit budget | `:features:budgets` | Adjust limits |
| `categories` | Category list | `:features:categories` | Manage categories |
| `categories/add` | Add category | `:features:categories` | Name + color picker |
| `reports` | Reports dashboard | `:features:reports` | Charts & summaries |
| `reports/monthly` | Monthly breakdown | `:features:reports` | Detailed period report |
| `reports/yearly` | Yearly overview | `:features:reports` | Annual trends |
| `settings` | Settings | `:features:settings` | Preferences, export, about |

#### Navigation Patterns

| Pattern | Behavior |
|---------|----------|
| **Bottom nav** | Each tab has its own navigation stack (save/restore state via `saveState`/`restoreState`) |
| **Deep link (notification)** | `expensetracker://transactions/{id}` → opens detail directly |
| **Deep link (widget)** | `expensetracker://transactions/add` → opens add screen |
| **Up navigation** | Pop back stack; if at root of tab, switch to Home tab |
| **Unsaved changes** | Show confirmation dialog before navigating away from forms |
| **Pull-to-refresh** | On transaction list, budget list, reports (re-fetch from remote) |
| **Swipe-to-delete** | Transaction list items → reveals delete action with undo snackbar |

### Screen-by-Screen Design

---

#### 1. HOME / DASHBOARD

```
┌──────────────────────────────────────────┐
│  ←▶ Expense Tracker                🔔 ⚙️ │  TopAppBar (primaryContainer)
├──────────────────────────────────────────┤
│  ┌──────────────────────────────────┐    │
│  │  Total Spent                     │    │  Card (primary bg, R=24dp, elev=8dp)
│  │  $1,250.00                       │    │  displayMedium, Bold, OnPrimary
│  │  ┌──────────────────────────┐    │    │
│  │  │ ▲ 12% vs last month     │    │    │  Comparison badge
│  │  └──────────────────────────┘    │    │
│  └──────────────────────────────────┘    │
│                                          │
│  ┌─ Budget Overview ─────────────────┐   │
│  │  [Food ▓▓░░]  [Bills ▓▓▓░]  [Tr…│   │  HorizontalScroll row
│  │  Each: 120×72dp card              │   │  Progress bar, label, amount
│  └────────────────────────────────────┘   │
│                                          │
│  Recent Transactions          → See All  │  titleLarge + TextButton
│  ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐     │
│  │  🍔 Groceries         -$45.00 │     │  ExpenseListItem
│  │     Today 2:30pm               │     │  H=72dp, R=12dp, elev=2dp
│  ├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┤     │
│  │  🚌 Bus fare          -$2.50  │     │
│  │     Today 8:15am               │     │
│  ├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┤     │
│  │  🛒 Amazon            -$89.99 │     │
│  └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘     │
│                                          │
│                                    [＋]  │  FAB (primary, elev=6dp)
├──────────────────────────────────────────┤
│  Home │ Trans │ Budget │ Rprts │ Settngs │  BottomNavBar
└──────────────────────────────────────────┘
```

**States:**

| State | UI |
|-------|----|
| **Loading** | 3 shimmer placeholder cards (total card skeleton + 2 list item skeletons) + `CircularProgressIndicator` behind translucent overlay |
| **Empty** | Total card shows `$0.00`; empty state illustration with "Your first expense is waiting to be tracked!" + "Add Expense" button |
| **Error** | Total card shows `$0.00`; `Snackbar` with "Could not load data. Check your connection." + "Retry" action |
| **Offline** | Subtle banner below TopAppBar: "You're offline — showing cached data" with offline icon |

**Interactions:**
- Tap **See All** → navigate to `transactions`
- Tap **transaction item** → navigate to `transactions/{id}`
- Tap **budget card** → navigate to `budgets`
- Tap **FAB** → open `AddExpenseSheet` (modal bottom sheet)
- Tap **notification bell** → open notification list (or deep link)
- Tap **gear icon** → navigate to `settings`

---

#### 2. ALL TRANSACTIONS

```
┌──────────────────────────────────────────┐
│  ← Transactions                   🔍 ＋  │  TopAppBar
├──────────────────────────────────────────┤
│  [All][Food][Transport][Shop][Bills][…]  │  FilterChipRow (horizontal scroll)
├──────────────────────────────────────────┤
│  ┌─ Search transactions ──────────────┐  │
│  │  🔍                                │  │  SearchBar (text field with icon)
│  └────────────────────────────────────┘  │
│                                          │
│  THIS MONTH — 6 transactions     $630.49│  Sticky header (date section)
│  ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐     │
│  │  🍔 Groceries         -$45.00 │     │  ← Swipe left → reveal [Delete]
│  │     Today 2:30pm               │     │     Swipe → Undo snackbar (5s)
│  ├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┤     │
│  │  🚌 Bus fare          -$2.50  │     │
│  └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘     │
│                                          │
│  YESTERDAY                       $89.99 │
│  ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐     │
│  │  🛒 Amazon            -$89.99 │     │
│  └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘     │
│                                          │
│  MON, MAY 22                    $312.00 │
│  ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐     │
│  │  💡 Electricity        -$120.00│     │
│  ├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┤     │
│  │  🏪 Costco             -$192.00│     │
│  └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘     │
│                                          │
│                              [Load more] │  Pagination button
├──────────────────────────────────────────┤
│  Home │ Trans │ Budget │ Rprts │ Settngs │
└──────────────────────────────────────────┘
```

**States:**

| State | UI |
|-------|----|
| **Loading** | 5 shimmer list item placeholders (pulsing gray bars mimicking icon + title + amount) |
| **Empty** | Large illustration (empty folder/receipt) + "No transactions yet" + "Add your first expense" button |
| **Empty (after filter)** | "No results for 'Transport'" with "Clear filters" chip button |
| **Error** | Error illustration + "Couldn't load transactions" + "Retry" button |
| **Offline** | Snackbar: "Showing cached data. Some transactions may not appear." |
| **Pull-to-refresh** | `pullToRefresh` modifier with `PullToRefreshContainer` |

**Interactions:**
- **Filter chip tap** → filter list by category (selected chip highlighted with `primaryContainer`)
- **Search** → filter by title (debounced 300ms)
- **Swipe left** on item → red Delete background with trash icon; after deletion show `Snackbar` "Deleted" + "Undo" (undo calls `restoreExpense` with 5s timeout)
- **Tap item** → navigate to `transactions/{id}` with shared element transition on category icon
- **Tap ＋** → navigate to `transactions/add` or open bottom sheet
- **Pull down** → refresh from remote API
- **Load more** → paginate (cursor-based, 20 per page)

---

#### 3. TRANSACTION DETAIL

```
┌──────────────────────────────────────────┐
│  ← Transactions                  ✎ 🗑    │  TopAppBar with Edit + Delete
├──────────────────────────────────────────┤
│                                          │
│              🍔                          │  Category icon (64dp circle)
│              Groceries                   │  bodyLarge, onSurfaceVariant
│                                          │
│              $45.00                      │  displayLarge, Bold, centered
│                                          │
│          Today · 2:30 PM                 │  bodyMedium, onSurfaceVariant
│                                          │
│  ┌──────────────────────────────────┐    │
│  │  Description                     │    │  Label "Description"
│  │  Weekly supermarket run          │    │  Value (multiline if long)
│  └──────────────────────────────────┘    │
│                                          │
│  ┌──────────────────────────────────┐    │
│  │  Category          Groceries ▾   │    │  Editable dropdown (edit mode)
│  └──────────────────────────────────┘    │
│                                          │
│  ┌──────────────────────────────────┐    │
│  │  Amount                   $45.00 ▾   │    │  Editable number field
│  └──────────────────────────────────┘    │
│                                          │
│  ┌──────────────────────────────────┐    │
│  │  Date & Time   Today 2:30 PM ▾   │    │  Editable date picker
│  └──────────────────────────────────┘    │
│                                          │
│  ┌──────────────────────────────────┐    │
│  │  Attach Receipt (optional)       │    │  Camera / Gallery button
│  └──────────────────────────────────┘    │
│                                          │
│  [            Save Changes          ]    │  Primary button (edit mode only)
│  [              Delete              ]    │  Destructive button (red, with confirm dialog)
└──────────────────────────────────────────┘
```

**States:**

| State | UI |
|-------|----|
| **Loading** | Full-screen shimmer (icon skeleton, 3 text line skeletons) |
| **Not found (404)** | "Transaction not found" illustration + "Go back" button |
| **Edit mode** | Fields become editable (OutlinedTextField / dropdown), Save button appears |
| **Delete confirm** | AlertDialog: "Delete this expense?" + "This action cannot be undone." + [Cancel] [Delete] |
| **Saving** | Button shows `CircularProgressIndicator` (12dp), disabled state |
| **Save success** | Pop back to list with updated data (shared element transition) |
| **Save error** | Snackbar "Could not save changes. Try again." |

**Interactions:**
- **Tap Edit (✎)** → toggle edit mode; fields become `OutlinedTextField`/`ExposedDropdownMenuBox`
- **Tap Delete (🗑)** → show confirm dialog
- **Confirm delete** → delete from DB, pop back to list with "Deleted" undo snackbar
- **Back navigation with unsaved changes** → dialog "Discard changes?" [Discard] [Keep editing]
- **Tap Save** → validate fields (title required, amount > 0), save to Room, pop back
- **Attach Receipt** → launches CameraX or photo picker (requires camera/storage permissions)

---

#### 4. ADD EXPENSE

```
╔══════════════════════════════════════════╗
║          Add New Expense                 ║  ←────── Drag handle
╠══════════════════════════════════════════╣
║                                          ║  ModalBottomSheet
║  ┌─ Title ──────────────────────────┐   ║  (top R=24dp)
║  │  e.g. Weekly groceries           │   ║
║  │  ┌──────────────────────────┐    │   ║  OutlinedTextField
║  │  │ Title cannot be empty    │    │   ║  Error state (red border + helper text)
║  │  └──────────────────────────┘    │   ║
║  └──────────────────────────────────┘   ║
║                                          ║
║  ┌─ Amount ────────────────────────┐   ║
║  │  45.00                         │   ║  OutlinedTextField (Decimal keyboard)
║  │  ┌──────────────────────────┐    │   ║
║  │  │ Enter a valid amount     │    │   ║  Error state
║  │  └──────────────────────────┘    │   ║
║  └──────────────────────────────────┘   ║
║                                          ║
║  ┌─ Category ─────────────────────┐   ║
║  │  🍔 Food                    ▾  │   ║  ExposedDropdownMenuBox
║  └──────────────────────────────────┘   ║
║                                          ║
║  ┌─ Date ─────────────────────────┐   ║
║  │  May 25, 2026               📅 │   ║  DatePicker trigger
║  └──────────────────────────────────┘   ║
║                                          ║
║  ┌─ Receipt (optional) ───────────┐   ║
║  │  📷 Tap to attach photo        │   ║  Camera/gallery button
║  └──────────────────────────────────┘   ║
║                                          ║
║  ┌──────────────────────────────────┐   ║
║  │        Save Expense              │   ║  Full-width Button (primary)
║  └──────────────────────────────────┘   ║
║                                          ║
╚══════════════════════════════════════════╝
```

**States:**

| State | UI |
|-------|----|
| **Validation error** | Individual field errors appear inline (red border, helper text below field) |
| **Saving** | Button shows progress indicator, all fields disabled |
| **Save success** | Sheet dismisses, new item appears in list with animation, Snackbar "Expense added" |
| **Save error** | Snackbar "Could not save expense" + "Retry" button |
| **Dismiss with unsaved data** | Dialog "Discard this expense?" [Discard] [Keep editing] |
| **Receipt attached** | Thumbnail preview (64×64dp) with X to remove |

**Interactions:**
- **Swipe down** → dismiss (with unsaved changes check)
- **Tap outside** → dismiss (with unsaved changes check)
- **Tap Save** → validate → call `viewModel.addExpense()` → dismiss on success
- **Tap category dropdown** → show dropdown with 6 categories (each with color dot + name)
- **Tap date** → show Material3 `DatePickerDialog`
- **Tap camera icon** → request permissions → launch camera / pick from gallery

---

#### 5. BUDGETS

```
┌──────────────────────────────────────────┐
│  ← Budgets                         ＋    │  TopAppBar
├──────────────────────────────────────────┤
│                                          │
│  ┌─── Monthly Overview ───────────────┐  │
│  │           $1,200 / $2,000          │  │  CircularProgressIndicator (R=60dp)
│  │               ▓▓▓▓▓▓▓░░░          │  │  Stroke width 12dp
│  │               60% used             │  │  Center text: percentage
│  │               ⏱ 18 days left      │  │  Subtitle below ring
│  └────────────────────────────────────┘  │
│                                          │
│  ┌─── 🍔 Food ───────────────────────┐  │
│  │  ▓▓▓▓▓▓▓▓░░░░░                   │  │  LinearProgressIndicator (H=8dp, R=4dp)
│  │  $450 used of $600 · 75%         │  │  Label + amount
│  │  ⏱ 18 days left                  │  │  Days remaining
│  └────────────────────────────────────┘  │
│                                          │
│  ┌─── 🚌 Transport ─────────────────┐   │
│  │  ▓▓▓▓▓░░░░░░░░░                  │   │
│  │  $80 used of $200 · 40%          │   │
│  │  ⏱ 18 days left                  │   │
│  └────────────────────────────────────┘  │
│                                          │
│  ┌─── 💡 Bills ─────────────────────┐   │
│  │  ▓▓▓▓▓▓▓▓▓▓▓▓▓▓ ← Red/Orange    │   │  ⚠ Exceeds 90% threshold
│  │  $500 used of $500 · 100% ⚠     │   │
│  │  ⏱ 18 days left                  │   │
│  └────────────────────────────────────┘  │
│                                          │
│  ┌─── 🎮 Entertainment ────────────┐   │
│  │  ░░░░░░░░░░░░░░                  │   │  Empty progress (0%)
│  │  $0 used of $100 · 0%           │   │
│  └────────────────────────────────────┘  │
├──────────────────────────────────────────┤
│  Home │ Trans │ Budget │ Rprts │ Settngs │
└──────────────────────────────────────────┘
```

**States:**

| State | UI |
|-------|----|
| **Loading** | Circular ring skeleton + 4 shimmer linear progress bars |
| **Empty (no budgets set)** | "No budgets yet" illustration + "Set a monthly budget to track spending" + "Create Budget" button |
| **Over budget (≥90%)** | Progress bar turns orange at 90%, red at 100%; warning icon appears; card border becomes `error` color |
| **Error** | Snackbar "Could not load budgets" |
| **Offline** | Banner: "Showing cached budget data" |

**Interactions:**
- **Tap ＋** → navigate to `budgets/add` (select category, set limit)
- **Tap budget card** → navigate to `budgets/{id}/edit` (adjust limit, view breakdown)
- **Long-press budget card** → context menu: [Edit] [Reset] [Delete]
- **Pull-to-refresh** → refresh from remote

---

#### 6. REPORTS / ANALYTICS

```
┌──────────────────────────────────────────┐
│  ← Reports                   May 2026 ▾  │  TopAppBar with period picker
├──────────────────────────────────────────┤
│                                          │
│  ┌─── Spending Breakdown ─────────────┐  │
│  │                                     │  │
│  │       ┌─────┐                      │  │  Donut/Pie chart
│  │    ┌──┤     ├──┐                   │  │  Each slice = category color
│  │    │  └─────┘  │                   │  │  Center total: $1,250
│  │    └───────────┘                   │  │
│  │                                     │  │
│  │  🍔 Food            $562   45% ▓▓▓▓│  │  Legend with color dot
│  │  🚌 Transport       $250   20% ▓▓  │  │
│  │  🛒 Shopping        $188   15% ▓   │  │
│  │  💡 Bills           $150   12% ▓   │  │
│  │  🎮 Entertainment   $100    8% ▓   │  │
│  └────────────────────────────────────┘  │
│                                          │
│  ┌─── Daily Trend ───────────────────┐  │
│  │                                     │  │  Line chart
│  │    ▲ $80                           │  │  X-axis: days of month
│  │    │     ╱╲    ╱╲                  │  │  Y-axis: amount
│  │    │   ╱  ╲  ╱  ╲                 │  │  Gradient fill below line
│  │    │ ╱    ╲╱    ╲___╱╲            │  │
│  │    │╱────────────────────╲──       │  │
│  │    └──────────────────────────     │  │
│  └────────────────────────────────────┘  │
│                                          │
│  ┌─── Top Expenses ──────────────────┐  │
│  │  1. 🛒 Amazon              $89.99 │  │  Ranked list
│  │  2. 🍔 Costco              $77.50 │  │  Index number + category icon
│  │  3. 💡 Electricity         $120.00│  │  Amount highlighted
│  └────────────────────────────────────┘  │
├──────────────────────────────────────────┤
│  Home │ Trans │ Budget │ Rprts │ Settngs │
└──────────────────────────────────────────┘
```

**States:**

| State | UI |
|-------|----|
| **Loading** | 3 chart skeleton placeholders (circle skeleton + line skeleton + list skeletons) |
| **Empty (no data)** | "No data for this period" illustration + "Add some expenses to see insights" |
| **Error** | "Could not load reports" + "Retry" button |
| **Offline** | Banner: "Some data may be outdated" |

**Interactions:**
- **Period picker dropdown** → switch between months (with prev/next arrows)
- **Tap pie slice / legend item** → filter all data to that category; section highlights
- **Tap chart data point** → show tooltip with date + amount
- **Long-press chart** → crosshair with value tracking

---

#### 7. CATEGORIES

```
┌──────────────────────────────────────────┐
│  ← Categories                       ＋   │  TopAppBar
├──────────────────────────────────────────┤
│                                          │
│  ┌── 🍔 Food ─────────────────────────┐ │
│  │   ████  (red bar indicator)         │ │  Category card
│  │   12 transactions · $450 this month │ │  Color indicator on left (4dp stripe)
│  │                                     │ │  Transaction count + amount
│  └────────────────────────────────────┘ │
│                                          │
│  ┌── 🚌 Transport ───────────────────┐  │
│  │   ████  (blue bar)                 │  │
│  │   8 transactions · $80 this month  │  │
│  └────────────────────────────────────┘  │
│                                          │
│  ┌── 🛒 Shopping ────────────────────┐  │
│  │   ████  (orange bar)              │  │
│  │   5 transactions · $200 this month│  │
│  └────────────────────────────────────┘  │
│                                          │
│  ┌── 💡 Bills ───────────────────────┐  │
│  │   ████  (purple bar)              │  │
│  │   3 transactions · $150 this month│  │
│  └────────────────────────────────────┘  │
│                                          │
│  ┌── 🎮 Entertainment ───────────────┐  │
│  │   ████  (pink bar)                │  │
│  │   2 transactions · $100 this month│  │
│  └────────────────────────────────────┘  │
│                                          │
│  ┌── Other ──────────────────────────┐  │
│  │   ████  (grey bar)                │  │
│  │   1 transaction · $15 this month  │  │
│  └────────────────────────────────────┘  │
│                                          │
│  ┌─── Manage Categories ─────────────┐   │
│  │  [Food] [Transport] [Shop] [+]   │   │  FlowRow of chips
│  │  Tap to rename · Long-press drag  │   │  Reorder capability
│  └────────────────────────────────────┘  │
├──────────────────────────────────────────┤
│  Home │ Trans │ Budget │ Rprts │ Settngs │
└──────────────────────────────────────────┘
```

**States:**

| State | UI |
|-------|----|
| **Loading** | 6 shimmer category card skeletons |
| **Empty** | "No categories" — only "Other" exists by default |
| **Error** | Snackbar "Could not load categories" |

**Interactions:**
- **Tap ＋** → dialog to add new category: name field + color picker (6 color grid)
- **Tap category card** → navigate to filtered transaction list (e.g., `transactions?category=Food`)
- **Tap chip rename** → inline edit / dialog to rename (default categories may be locked)
- **Long-press drag** → reorder categories in chip list

---

#### 8. SETTINGS

```
┌──────────────────────────────────────────┐
│  ← Settings                              │  TopAppBar
├──────────────────────────────────────────┤
│                                          │
│  ┌─── Account ───────────────────────┐  │
│  │  👤  John Doe                      │  │  Profile row (avatar + name)
│  │     john.doe@email.com             │  │  Subtitle
│  └────────────────────────────────────┘  │
│                                          │
│  ┌─── Preferences ───────────────────┐  │
│  │  🌐 Currency          USD ▾       │  │  Dropdown: USD / EUR / GBP / etc. (DataStore)
│  │  🌙 Theme         System ▾       │  │  Dropdown: Light / Dark / System
│  │  📅 Budget Period  Monthly ▾     │  │  Dropdown: Weekly / Monthly / Yearly
│  │  🔢 Default Category   Other ▾   │  │  Dropdown: pick default category
│  └────────────────────────────────────┘  │
│                                          │
│  ┌─── Notifications ────────────────┐  │
│  │  🔔 Budget alerts              ON │  │  Switch
│  │  📊 Weekly report             OFF │  │  Switch
│  └────────────────────────────────────┘  │
│                                          │
│  ┌─── Data ──────────────────────────┐  │
│  │  📤 Export as CSV                 │  │  Menu row (opens share sheet)
│  │  📥 Import from CSV               │  │  Menu row (opens file picker)
│  │  ☁️ Sync with server          OFF │  │  Switch (backend sync toggle)
│  │  🗑  Clear all data               │  │  Destructive (red text, confirm dialog)
│  └────────────────────────────────────┘  │
│                                          │
│  ┌─── About ─────────────────────────┐  │
│  │  ℹ️  Version                  1.0.0│  │
│  │  📄 Open source licenses          │  │  → LicensesActivity
│  │  ⭐ Rate the app                  │  │  → Play Store listing
│  └────────────────────────────────────┘  │
├──────────────────────────────────────────┤
│  Home │ Trans │ Budget │ Rprts │ Settngs │
└──────────────────────────────────────────┘
```

**States:**

| State | UI |
|-------|----|
| **Loading** | Shimmer rows for each section |
| **Clear data confirm** | AlertDialog: "This will delete ALL your data. Cannot be undone." [Cancel] [Delete Everything] (red button) |
| **Export success** | Snackbar "CSV exported" + share sheet opens automatically |
| **Export error** | Snackbar "Could not export data" |
| **Import error** | Snackbar "Invalid file format" |
| **Theme change** | Instant recomposition (DataStore flow triggers theme switch) |

**Interactions:**
- **Dropdown selections** → persist to DataStore immediately
- **Toggle switches** → persist to DataStore immediately
- **Export CSV** → generate CSV from Room DB → share via Android share intent
- **Import CSV** → launch file picker → parse → bulk insert with conflict resolution (dialog for duplicate handling)
- **Clear all data** → two-step confirmation: dialog → clear Room + DataStore → restart Home screen
- **Sync toggle** → enable/disable periodic background sync with Ktor backend

### Reusable Component Library

Build these as reusable composables in a shared UI module:

| Component | Height | Shape | Key Props | States |
|-----------|--------|-------|-----------|--------|
| `AppTopBar` | 64dp | — | title, navIcon, actions | — |
| `TotalCard` | auto | R=24dp | amount, percentage, period | loading (shimmer) |
| `ExpenseListItem` | 72dp | R=12dp, elev=2dp | icon, title, subtitle, amount, swipe | swipe-delete, shimmer |
| `FilterChipRow` | 40dp | R=20dp | items, selected, onSelect | — |
| `ProgressBar` | 8dp | R=4dp | progress (0.0–1.0), color | determinate / indeterminate |
| `CircularProgressIndicator` | 120dp | — | progress, strokeWidth, size | determinate / indeterminate |
| `BudgetCard` | auto | R=12dp | category, used, limit, progress, daysLeft | warning (≥90%), over (100%) |
| `CategoryIcon` | 40dp | Circle | category, size | color mapped from enum |
| `AddExpenseSheet` | ~70% | R=24dp (top) | — | validation, saving, success, error, unsaved-dismiss |
| `EmptyState` | auto | — | illustration, title, subtitle, action | — |
| `ErrorState` | auto | — | message, onRetry | — |
| `ShimmerPlaceholder` | auto | configurable | width, height, shape | — |
| `ConfirmDialog` | auto | — | title, message, confirmLabel, isDestructive | — |
| `Snackbar` | 48dp | R=12dp | message, action, actionLabel | undo, dismiss |
| `SectionHeader` | 32dp | — | title, action, actionLabel | — |

### User Flows (Critical Paths)

#### Flow A: Log a new expense (happy path)
```
1. User taps FAB (＋) on Home
2. → AddExpenseSheet slides up
3. User fills: Title, Amount, Category, Date (+ optional photo)
4. User taps "Save Expense"
5. → Button shows spinner, fields disabled
6. → Expense saved to Room via ViewModel
7. → Sheet dismisses automatically
8. → New item appears at top of Recent Transactions list (with animateItemPlacement)
9. → Total Spent card updates (animated number count-up)
10. → Snackbar: "Expense added" (2s, auto-dismiss)
```

#### Flow B: Delete with undo
```
1. User swipes left on an ExpenseListItem
2. → Red Delete background revealed with trash icon
3. → On swipe completion:
     a. Expense removed from list (animateItemRemoval)
     b. Total Spent updates
     c. Snackbar appears: "Deleted" [Undo] (5s timeout)
4. If user taps [Undo] within 5s:
     → Expense restored to original position in list
     → Snackbar: "Restored"
5. If 5s expires:
     → Expense deleted from Room permanently
     → No further action
```

#### Flow C: Navigate away with unsaved data
```
1. User fills AddExpenseSheet (or Edit mode on TransactionDetail)
2. User taps back / swipes down / taps outside
3. → If any field has been modified:
     → AlertDialog: "Discard changes?" [Discard] [Keep editing]
4. [Discard] → dismiss, no save
5. [Keep editing] → dialog closes, user continues editing
```

#### Flow D: Offline behavior
```
1. Device loses connectivity
2. → ConnectivityManager callback triggers
3. → Subtle banner appears below TopAppBar: "You're offline — showing cached data"
4. → All CRUD operations go only to Room (local-first)
5. → Pull-to-refresh shows "No connection" in refresh indicator
6. → Network-dependent features (sync, import/export) disabled with tooltip
7. When connectivity returns:
     → Banner dismisses
     → Silent background sync pushes local changes to backend (if sync enabled)
     → Snackbar: "Back in sync!"
```

---

## Build & Run

### Android Client

```bash
# Build debug APK
./gradlew :android:app:assembleDebug

# Run unit tests
./gradlew :android:app:testDebugUnitTest

# Run a single test
./gradlew :android:app:testDebugUnitTest --tests "dev.oruizp.expensetracker.ExampleUnitTest"

# Full Android build (assemble + lint + test)
./gradlew :android:app:build
```

### Backend

```bash
# Build + test
./gradlew :backend:build :backend:test

# Run Ktor server (port 8080)
./gradlew :backend:run
```

### Full Project Build

```bash
./gradlew build
```

---

## Testing Strategy

| Layer | Testing Approach | Tools |
|-------|-----------------|-------|
| **Presentation (UI)** | Compose UI tests with `ComposeTestRule`; screenshot tests | `androidx.compose.ui.test.junit4` |
| **ViewModel** | Unit test with fake repository + `kotlinx-coroutines-test` | JUnit 4, Turbine (Flow testing) |
| **Domain** | Pure unit tests for use cases | JUnit 4 |
| **Data (Local)** | Room in-memory database tests with `@RunWith(AndroidJUnit4::class)` | `androidx.test.runner.AndroidJUnit4` |
| **Data (Remote)** | MockWebServer for Retrofit API tests | OkHttp MockWebServer |
| **Backend** | Ktor `testHost` for route/controller tests | `ktor-server-test-host`, JUnit 4 |
| **Integration** | End-to-end: Android test + backend test server | UI tests + test server |

---

## Roadmap

### Phase 1 — Foundation ✓
- Room database, DAO, Entity
- Home screen with total card + transaction list
- Add expense bottom sheet
- Material 3 theme (light/dark/dynamic)

### Phase 2 — Navigation & Scaffolding
- Bottom navigation bar with 5 tabs
- NavHost with route definitions
- Empty placeholder screens for all feature modules
- TopAppBar with back navigation

### Phase 3 — Transactions
- Full transaction list with filter chips
- Search bar (debounced)
- Transaction detail screen (view / edit / delete)
- Swipe-to-delete with undo snackbar
- Pull-to-refresh
- Pagination (load more)

### Phase 4 — Budgets
- Monthly budget overview (circular + linear progress)
- Per-category budget cards
- Add/edit budget sheet
- Over-budget warnings (90% → orange, 100% → red)

### Phase 5 — Reports
- Pie/donut chart for spending breakdown
- Line chart for daily trends
- Period picker (monthly, yearly)
- Top expenses list
- Chart interaction (tap slice, tooltips)

### Phase 6 — Categories
- Category list with transaction counts
- Add/edit category (name + color)
- Reorder categories

### Phase 7 — Settings
- Preferences (currency, theme, budget period)
- Data export/import (CSV)
- Clear all data with confirmation
- About section

### Phase 8 — Error Handling & Polish
- All error/loading/empty states across every screen
- Offline banner + offline-first behavior
- Shimmer loading placeholders
- Animations (item add/remove, card transitions, page transitions)
- Input validation refinement
- Accessibility (contentDescription, touch targets)

### Phase 9 — Backend Sync & Advanced
- Ktor REST API for expenses CRUD
- Retrofit integration on client
- Sync service (work manager)
- Authentication (optional)
- Push notifications for budget alerts
- Receipt photo capture (CameraX)
- Location tagging for transactions

---

## License

MIT
