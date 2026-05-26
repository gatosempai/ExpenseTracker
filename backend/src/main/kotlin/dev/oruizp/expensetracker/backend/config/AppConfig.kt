package dev.oruizp.expensetracker.backend.config

data class AppConfig(
    val database: DatabaseConfig = DatabaseConfig(),
    val server: ServerConfig = ServerConfig(),
)

data class DatabaseConfig(
    val driver: String = getEnv("DB_DRIVER", "org.h2.Driver"),
    val url: String = getEnv("DB_URL", "jdbc:h2:file:./data/expense_tracker;NON_KEYWORDS=NAME"),
    val user: String = getEnv("DB_USER", "sa"),
    val password: String = getEnv("DB_PASSWORD", ""),
    val poolSize: Int = getEnv("DB_POOL_SIZE", "10").toInt(),
    val migrationsEnabled: Boolean = getEnv("DB_MIGRATIONS_ENABLED", "true").toBoolean(),
)

data class ServerConfig(
    val port: Int = getEnv("SERVER_PORT", "8080").toInt(),
    val host: String = getEnv("SERVER_HOST", "0.0.0.0"),
    val corsAllowedOrigins: List<String> = getEnv("CORS_ORIGINS", "http://localhost:8081").split(","),
)

fun getEnv(key: String, default: String): String = System.getenv(key) ?: default
