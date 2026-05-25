# AGENTS.md — ExpenseTracker

## Build & Test

```bash
./gradlew build                              # full build
./gradlew :android:app:assembleDebug          # Android APK only
./gradlew :android:app:testDebugUnitTest      # Android unit tests
./gradlew :android:app:testDebugUnitTest --tests "dev.oruizp.expensetracker.ExampleUnitTest"  # single test
./gradlew :backend:build && :backend:test     # backend build + test
./gradlew :backend:run                        # start Ktor server (port 8080)
./gradlew lint                                # Android lint
```

CI runs `assembleDebug + testDebugUnitTest` (android) and `:backend:build + :backend:test` (backend) on push/PR to `main`.

## Project Structure

11 modules, 3 groups:

| Group | Modules |
|-------|---------|
| **Android client** | `:android:app`, `:android:core`, `:android:data`, `:android:domain`, `:android:features:{transactions,budgets,categories,reports,settings}` |
| **Backend** | `:backend` |
| **KMP shared** | `:common` (Android + iOS) |

**Current state:** Only `:app` and `:backend` have real code. All other modules are empty scaffolding with placeholder tests.

## Architecture

- **Client:** Clean architecture — `:app` (Compose UI + Room) → `:features:*` (currently empty) → `:data` (empty) → `:domain` (empty). Dependency order: `features → core/data/domain`.
- **Backend:** Ktor (Netty, port 8080) + Exposed ORM + Koin DI. Entry: `dev.oruizp.expensetracker.backend.ApplicationKt`. H2 in-memory dev / PostgreSQL prod.
- **Common:** KMP expect/actual (`Platform.kt`) for Android + iOS.

## Key Conventions

- **Package:** `dev.oruizp.expensetracker` (client), `dev.oruizp.expensetracker.backend` (server)
- **ViewModels:** `AndroidViewModel` + `StateFlow` for reactive state
- **Room:** KSP annotation processing (`ksp(libs.androidx.room.compiler)`), singleton `ExpenseDatabase`, `fallbackToDestructiveMigration()`
- **Backend DI:** Koin only (no Hilt on server)
- **Version catalog:** `gradle/libs.versions.toml` — always use `libs.*` references in build files

## Stack

| Tool | Version |
|------|---------|
| AGP | 9.1.1 |
| Kotlin | 2.2.10 |
| Gradle | 9.3.1 |
| Compose BOM | 2024.09.00 |
| Room | 2.7.0 |
| Ktor | 2.3.12 |
| Koin | 3.5.6 |
| minSdk / targetSdk / compileSdk | 24 / 36 / 36 |
| Java | 11 |

## Testing

- JUnit 4, `kotlinx-coroutines-test` available
- `@RunWith(AndroidJUnit4::class)` for instrumented tests
- No Detekt or ktlint configured yet; Android Lint plugin declared but without custom config.
