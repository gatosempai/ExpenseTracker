# 📱 Expense Tracker – Offline‑First Android + Kotlin Backend (2026)

## 🎯 Project Overview

Build a **fully offline‑capable expense tracker** with receipt photo support.  
All user data is stored locally first; background sync updates a remote backend when connectivity is available.  
The backend exposes **both REST and GraphQL** APIs and is written in **Kotlin**.

**Primary goal:** Refresh and modernise your Android skills using the 2026 ecosystem.

---

## ✨ Core Features

| Feature | Description |
|---------|-------------|
| **Expense management** | Add, edit, delete expenses (amount, category, date, note, receipt photo) |
| **Offline‑first** | All CRUD works without internet; sync queue handles replay |
| **Receipt OCR** | Use ML Kit to auto‑fill amount, date, merchant from photo |
| **Analytics** | Charts by category / month (MPAndroidChart or Compose custom) |
| **Sync status** | Visual indicator of pending sync / last sync time |
| **Authentication** | Biometric (optional) + JWT token from backend |
| **Multi‑platform ready** | Shared KMP business logic (optional stretch) |

---

## 🧱 Tech Stack (2026 Edition)

### Android Client
- **UI:** Jetpack Compose + Material 3
- **State:** ViewModel + Kotlin Flow + Compose state hoisting
- **Local DB:** Room (SQLite) with type converters for `Date`, `BigDecimal`
- **Background sync:** WorkManager (periodic + network‑triggered)
- **Image handling:** CameraX, Coil, ML Kit Text Recognition
- **DI:** Hilt
- **Networking:** Retrofit (REST) + Apollo Kotlin (GraphQL) – both with offline cache
- **Testing:** JUnit, Robolectric, MockK, Turbine (Flow test)

### Backend (Kotlin)
- **Framework:** Ktor (or Spring Boot with `graphql-kotlin-spring-starter`)
- **GraphQL:** `graphql-kotlin-server` – queries for expenses / categories, mutations for writes
- **REST:** Ktor routes – file upload (`/upload/receipt`)
- **Persistence:** Exposed (Ktor) or Spring Data JPA – using PostgreSQL (prod) / H2 (test)
- **Auth:** JWT (jjwt) with refresh tokens

### CI/CD
- **Platform:** GitHub Actions
- **Lint:** Detekt + ktlint
- **Testing:** Android unit tests + backend integration tests
- **Build & deploy:**  
  - Android → Firebase App Distribution (test) / Google Play (prod)  
  - Backend → Railway / Fly.io (Docker container)

---

## 🔁 Offline‑First Architecture (Deep Dive)

### Local Schema (Room)
```kotlin
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val amount: BigDecimal,
    val category: String,
    val date: LocalDate,
    val note: String?,
    val receiptUri: String?,      // local file path
    val syncStatus: SyncStatus,   // PENDING, SYNCED, CONFLICT
    val lastModified: Long
)

@Entity(tableName = "sync_queue")
data class SyncOperation(
    @PrimaryKey val operationId: String,
    val entityId: String,
    val operationType: OperationType, // INSERT, UPDATE, DELETE
    val payload: String,              // JSON of the expense
    val createdAt: Long
)
```

### Sync Flow (WorkManager)
1. **Local write** → insert into `sync_queue` + update local `expenses`.
2. **Worker** runs every 30 min (or on network available):
   - Fetch pending operations from queue.
   - Send to GraphQL mutations in order (batched if possible).
   - On success → delete queue entries + mark expense as `SYNCED`.
   - On failure → keep in queue, backoff retry.

### Conflict Resolution
- **Server wins** for amounts / categories (overwrite local if `remote.lastModified > local.lastModified`).
- **Client wins** for temporary draft notes (mark as `pending_sync`, don't overwrite local until user resolves).

---

## 🌐 Backend API Design

### GraphQL (main data operations)
```graphql
type Query {
  expenses(since: DateTime): [Expense!]!
  categories: [Category!]!
}

type Mutation {
  addExpense(input: ExpenseInput!): Expense!
  updateExpense(id: ID!, input: ExpenseInput!): Expense!
  deleteExpense(id: ID!): Boolean!
}

input ExpenseInput {
  amount: Decimal!
  category: String!
  date: Date!
  note: String
}
```

### REST (auxiliary)
- `POST /upload/receipt` → multipart form, returns image URL.
- `GET /receipt/{id}` → serves image binary.

---

## 🚀 CI/CD Pipeline (GitHub Actions)

Create `.github/workflows/ci_cd.yml`:

```yaml
name: CI/CD

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  android-ci:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - name: Cache Gradle
        uses: actions/cache@v3
        with:
          path: ~/.gradle/caches
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}
          restore-keys: ${{ runner.os }}-gradle-
      - name: Lint & Test
        run: |
          ./gradlew detekt
          ./gradlew :app:testDebugUnitTest
      - name: Build APK
        run: ./gradlew :app:assembleDebug
      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: expense-tracker-debug
          path: app/build/outputs/apk/debug/

  backend-ci:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
      - name: Run backend tests
        run: ./gradlew :backend:test
      - name: Build fat JAR
        run: ./gradlew :backend:buildFatJar
      - name: Upload JAR
        uses: actions/upload-artifact@v4
        with:
          name: backend-jar
          path: backend/build/libs/*-all.jar

  android-cd:
    needs: android-ci
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Build release AAB
        run: ./gradlew :app:bundleRelease
      - name: Sign & deploy to Firebase
        uses: wzieba/Firebase-Distribution-GitHub-Action@v1
        with:
          appId: ${{ secrets.FIREBASE_APP_ID }}
          token: ${{ secrets.FIREBASE_TOKEN }}
          groups: testers
          file: app/build/outputs/bundle/release/app-release.aab

  backend-cd:
    needs: backend-ci
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Deploy to Railway
        run: |
          npm install -g @railway/cli
          railway up --service=expense-backend
        env:
          RAILWAY_TOKEN: ${{ secrets.RAILWAY_TOKEN }}
```

> Replace secrets in GitHub repository settings.

---

## 💡 Insights & Learning Opportunities

### Why offline‑first in 2026?
- User expectation: apps must work without a stable internet (trains, planes, roaming).
- It forces you to think about **data consistency**, **conflict resolution**, and **background tasks** – skills that separate junior from senior devs.

### GraphQL + REST together – good or bad?
- GraphQL reduces over‑fetching for complex queries (e.g., monthly reports with nested categories).
- REST remains simpler for binary uploads (multipart forms). Pragmatic choice.

### Kotlin everywhere
- Sharing DTOs, validation logic, and even GraphQL queries between Android and backend becomes possible with **Kotlin Multiplatform**. Consider adding a `common` module for `Expense` model and sync contracts.

### CI/CD non‑negotiable
- Automating lint, tests, and deployment gives you **confidence** to refactor fearlessly – essential when you’re learning and experimenting.

### Potential pitfalls
- **WorkManager + Room** – ensure transactions are atomic to avoid duplicate sync.
- **ML Kit receipt OCR** – works offline but accuracy varies; always let user edit.
- **GraphQL schema evolution** – use `@deprecated` and version your queries (Apollo supports).

### Stretch goals after MVP
- **Push notifications** (Firebase Cloud Messaging) to trigger sync.
- **End‑to‑end encryption** before data leaves the device.
- **Wear OS companion** – show recent expenses.

---

## 📁 Suggested Project Structure

```
expense-tracker/
├── android/                         # Android app module
│   ├── app/
│   ├── core/
│   ├── data/
│   ├── domain/
│   └── features/
├── backend/                         # Ktor project
│   ├── src/main/kotlin/
│   ├── src/test/kotlin/
│   └── build.gradle.kts
├── common/                          # (optional) KMP shared code
├── .github/workflows/ci_cd.yml
├── gradle/
├── settings.gradle.kts
└── README.md
```

---

## ✅ Next Steps for You

1. **Clone a starter** – Use [Android Studio Koala](https://developer.android.com/studio) (2026.1+).
2. **Implement offline local DB first** – Room + fake repository.
3. **Add sync layer** – WorkManager + queue.
4. **Build the backend** – Minimal GraphQL endpoint for expenses.
5. **Integrate CI/CD** – Watch the pipeline run on every push.
6. **Add receipt OCR** – ML Kit offline model.

> Every feature you finish will directly refresh a specific skill: Compose UI, coroutines, GraphQL clients, Room migrations, conflict resolution, or GitHub Actions.

---

**Now go build it. 🚀**  
If you get stuck on any part, refer back to this blueprint – it already contains the answers to 90% of architectural questions.


---

You can develop the **entire project** using **Android Studio** – including the Android client, the Kotlin backend, and even test CI/CD scripts locally.

Here’s how Android Studio covers each part:

| Component | Supported in Android Studio? | Notes |
|-----------|----------------------------|-------|
| **Android app** (Compose, Room, WorkManager, etc.) | ✅ Fully supported | Native project type. |
| **Kotlin backend** (Ktor / Spring Boot) | ✅ Fully supported | Android Studio is IntelliJ IDEA under the hood – it can open any Gradle/Kotlin JVM project. |
| **GraphQL schema & codegen** | ✅ Via plugins | Install **Apollo GraphQL** plugin for `.graphql` files and code generation. |
| **CI/CD scripts** (GitHub Actions YAML) | ✅ Via YAML plugin | Syntax highlighting and validation. |
| **Local Docker / Railway testing** | ✅ Terminal + plugins | Built‑in terminal, Docker plugin. |

---

## 🛠️ How to set it up in Android Studio

### 1. Single project (recommended)
Create a **multi‑module Gradle project**:
- `app/` – Android application module.
- `backend/` – Kotlin/JVM module with `application` plugin.
- `common/` – (optional) shared Kotlin code.

File → New → New Module → **Java or Kotlin Library** for backend.

### 2. Two separate projects (simpler)
- Open Android Studio for the Android app.
- File → Open → select the backend folder (treated as a separate IntelliJ project).
- Switch between windows or use **File → Open Recent**.

---

## ⚠️ Practical tips

- **Run backend from Android Studio** – Create a run configuration:  
  `Main class: com.expense.backend.ApplicationKt` (Ktor) or `SpringApplication`.
- **Test GraphQL locally** – Use the built‑in HTTP client (`.http` files) or install **HTTP Client** plugin.
- **Room schema inspection** – Android Studio’s Database Inspector works with the emulator’s Room DB.
- **GitHub Actions locally** – Use the `act` tool (terminal) to simulate CI/CD; Android Studio can run shell scripts via a **External Tool**.

---

## 🔍 Limitations (minor)
- No **visual designer** for backend code – but you don’t need one.
- Some backend frameworks like **Micronaut** have IntelliJ Ultimate‑only features; Ktor/Spring Boot work fine in Android Studio (Community edition).

---

## ✅ Verdict

**Go ahead with Android Studio.**  
You’ll have a single environment to write, run, debug, and version‑control both the mobile app and the Kotlin backend. Only if you later need advanced backend profiling (e.g., JFR, database client) would you consider IntelliJ Ultimate – but it’s not required.

> **Pro tip:** Use the same `.idea` folder for both modules, and disable Android‑only inspections for the backend module via `File → Settings → Editor → Inspections → uncheck Android`.

