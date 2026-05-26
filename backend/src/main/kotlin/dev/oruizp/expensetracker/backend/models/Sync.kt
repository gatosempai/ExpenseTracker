package dev.oruizp.expensetracker.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class SyncInput(
    val clientId: String,
    val lastSyncedAt: String,
    val operations: List<SyncOperation>,
)

@Serializable
data class SyncOperation(
    val operationType: SyncOperationType,
    val entityType: SyncEntityType,
    val localId: String,
    val serverId: String? = null,
    val data: String,
    val timestamp: String,
    val conflictVersion: Int = 1,
)

enum class SyncOperationType { CREATE, UPDATE, DELETE }

enum class SyncEntityType { EXPENSE, BUDGET, CATEGORY }

@Serializable
data class SyncResult(
    val success: Boolean,
    val applied: List<SyncApplied>,
    val conflicts: List<SyncConflict>,
    val serverTimestamp: String,
)

@Serializable
data class SyncApplied(
    val operationId: String,
    val serverId: String,
    val entity: SyncEntityType,
)

@Serializable
data class SyncConflict(
    val operationId: String,
    val serverId: String,
    val entity: SyncEntityType,
    val serverVersion: Int,
    val serverData: String,
    val resolution: ConflictResolution,
)

enum class ConflictResolution { CLIENT_WINS, SERVER_WINS, MANUAL }
