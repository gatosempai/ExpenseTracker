package dev.oruizp.expensetracker.backend.graphql.resolvers

import com.expediagroup.graphql.server.operations.Query
import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.services.SyncService

class SyncResolver(private val syncService: SyncService) : Query {

    suspend fun syncBatch(inputs: List<SyncInput>): SyncResult {
        val results = inputs.map { syncService.processBatch(it) }
        return results.lastOrNull() ?: SyncResult(
            success = true,
            applied = emptyList(),
            conflicts = emptyList(),
            serverTimestamp = java.time.Instant.now().toString(),
        )
    }
}
