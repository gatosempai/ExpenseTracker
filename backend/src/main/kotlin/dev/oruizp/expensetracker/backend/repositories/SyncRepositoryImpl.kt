package dev.oruizp.expensetracker.backend.repositories

import dev.oruizp.expensetracker.backend.database.SyncLogTable
import dev.oruizp.expensetracker.backend.database.dbQuery
import dev.oruizp.expensetracker.backend.models.*
import org.jetbrains.exposed.sql.insert

class SyncRepositoryImpl : SyncRepository {

    override suspend fun logOperation(
        userId: Int,
        clientId: String,
        operationType: SyncOperationType,
        entityType: SyncEntityType,
        entityId: Int?,
        clientEntityId: String,
        payload: String,
        conflictOccurred: Boolean,
        resolution: String?,
    ) {
        dbQuery {
            SyncLogTable.insert { stmt ->
                stmt[SyncLogTable.userId] = userId
                stmt[SyncLogTable.clientId] = clientId
                stmt[SyncLogTable.operationType] = operationType.name
                stmt[SyncLogTable.entityType] = entityType.name
                stmt[SyncLogTable.entityId] = entityId
                stmt[SyncLogTable.clientEntityId] = clientEntityId
                stmt[SyncLogTable.payload] = payload
                stmt[SyncLogTable.conflictOccurred] = conflictOccurred
                stmt[SyncLogTable.resolution] = resolution
            }
        }
    }
}
