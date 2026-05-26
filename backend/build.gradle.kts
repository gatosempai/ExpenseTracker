plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.graphql.gradle)
    application
}

application {
    mainClass.set("dev.oruizp.expensetracker.backend.ApplicationKt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

graphql {
    schema {
        packages = listOf(
            "dev.oruizp.expensetracker.backend.graphql.resolvers",
            "dev.oruizp.expensetracker.backend.models",
        )
    }
}

dependencies {
    // Ktor
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.serialization.json)

    // GraphQL
    implementation(libs.graphql.kotlin.ktor.server)

    // Koin DI
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger)

    // Exposed ORM
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.json)
    implementation(libs.exposed.java.time)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Databases
    implementation(libs.h2.database)
    implementation(libs.postgresql.jdbc)
    implementation(libs.hikari.cp)

    // Migrations
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)

    // Logging
    implementation(libs.logback.classic)
    implementation(libs.kotlinx.coroutines.core)

    // Test
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
