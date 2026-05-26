pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ExpenseTracker"

// Android app modules
include(":android:app")
include(":android:core")
include(":android:data")
include(":android:domain")

// Feature submodules
include(":android:features:transactions")
include(":android:features:budgets")
include(":android:features:categories")
include(":android:features:home")
include(":android:features:reports")
include(":android:features:settings")

// Backend Ktor module
include(":backend")

// KMP shared module (optional)
include(":common")
