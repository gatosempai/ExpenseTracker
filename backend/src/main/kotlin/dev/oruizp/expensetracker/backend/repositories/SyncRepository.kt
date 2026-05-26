package dev.oruizp.expensetracker.backend.repositories

import dev.oruizp.expensetracker.backend.models.*

interface SyncRepository {
    suspend fun logOperation(
        userId: Int,
        clientId: String,
        operationType: SyncOperationType,
        entityType: SyncEntityType,
        entityId: Int?,
        clientEntityId: String,
        payload: String,
        conflictOccurred: Boolean = false,
        resolution: String? = null,
    )
}
