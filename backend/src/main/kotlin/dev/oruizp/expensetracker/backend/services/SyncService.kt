package dev.oruizp.expensetracker.backend.services

import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.repositories.ExpenseRepository
import dev.oruizp.expensetracker.backend.repositories.SyncRepository
import kotlinx.serialization.json.Json
import java.time.Instant

class SyncService(
    private val expenseRepository: ExpenseRepository,
    private val categoryService: CategoryService,
    private val budgetService: BudgetService,
    private val syncRepository: SyncRepository,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun processBatch(input: SyncInput): SyncResult {
        val applied = mutableListOf<SyncApplied>()
        val conflicts = mutableListOf<SyncConflict>()

        for (op in input.operations) {
            try {
                when (op.entityType) {
                    SyncEntityType.EXPENSE -> processExpenseOp(op, input.clientId, applied, conflicts)
                    SyncEntityType.CATEGORY -> processCategoryOp(op, input.clientId, applied, conflicts)
                    SyncEntityType.BUDGET -> processBudgetOp(op, input.clientId, applied, conflicts)
                }
            } catch (e: Exception) {
                conflicts.add(
                    SyncConflict(
                        operationId = op.localId,
                        serverId = "",
                        entity = op.entityType,
                        serverVersion = op.conflictVersion,
                        serverData = "{}",
                        resolution = ConflictResolution.SERVER_WINS,
                    )
                )
            }
        }

        return SyncResult(
            success = conflicts.isEmpty(),
            applied = applied,
            conflicts = conflicts,
            serverTimestamp = Instant.now().toString(),
        )
    }

    private suspend fun processExpenseOp(
        op: SyncOperation,
        clientId: String,
        applied: MutableList<SyncApplied>,
        conflicts: MutableList<SyncConflict>,
    ) {
        when (op.operationType) {
            SyncOperationType.CREATE -> {
                val input = json.decodeFromString<CreateExpenseInput>(op.data)
                val expense = expenseRepository.create(input.copy(clientId = op.localId))
                syncRepository.logOperation(1, clientId, SyncOperationType.CREATE, SyncEntityType.EXPENSE, expense.id, op.localId, op.data)
                applied.add(SyncApplied(op.localId, expense.id.toString(), SyncEntityType.EXPENSE))
            }
            SyncOperationType.UPDATE -> {
                val serverId = op.serverId?.toIntOrNull() ?: return
                val existing = expenseRepository.findById(serverId)
                if (existing != null && existing.conflictVersion > op.conflictVersion) {
                    conflicts.add(
                        SyncConflict(
                            operationId = op.localId,
                            serverId = serverId.toString(),
                            entity = SyncEntityType.EXPENSE,
                            serverVersion = existing.conflictVersion,
                            serverData = json.encodeToString(Expense.serializer(), existing),
                            resolution = ConflictResolution.CLIENT_WINS,
                        )
                    )
                    return
                }
                val input = json.decodeFromString<UpdateExpenseInput>(op.data)
                expenseRepository.update(serverId, input)
                applied.add(SyncApplied(op.localId, serverId.toString(), SyncEntityType.EXPENSE))
            }
            SyncOperationType.DELETE -> {
                val serverId = op.serverId?.toIntOrNull() ?: return
                expenseRepository.delete(serverId)
                applied.add(SyncApplied(op.localId, serverId.toString(), SyncEntityType.EXPENSE))
            }
        }
    }

    private suspend fun processCategoryOp(
        op: SyncOperation,
        clientId: String,
        applied: MutableList<SyncApplied>,
        conflicts: MutableList<SyncConflict>,
    ) {
        when (op.operationType) {
            SyncOperationType.CREATE -> {
                val input = json.decodeFromString<CreateCategoryInput>(op.data)
                val category = categoryService.create(input)
                applied.add(SyncApplied(op.localId, category.id.toString(), SyncEntityType.CATEGORY))
            }
            SyncOperationType.UPDATE -> {
                val serverId = op.serverId?.toIntOrNull() ?: return
                val input = json.decodeFromString<UpdateCategoryInput>(op.data)
                categoryService.update(serverId, input)
                applied.add(SyncApplied(op.localId, serverId.toString(), SyncEntityType.CATEGORY))
            }
            SyncOperationType.DELETE -> {
                val serverId = op.serverId?.toIntOrNull() ?: return
                categoryService.delete(serverId)
                applied.add(SyncApplied(op.localId, serverId.toString(), SyncEntityType.CATEGORY))
            }
        }
    }

    private suspend fun processBudgetOp(
        op: SyncOperation,
        clientId: String,
        applied: MutableList<SyncApplied>,
        conflicts: MutableList<SyncConflict>,
    ) {
        when (op.operationType) {
            SyncOperationType.CREATE -> {
                val input = json.decodeFromString<CreateBudgetInput>(op.data)
                val budget = budgetService.create(input)
                applied.add(SyncApplied(op.localId, budget.id.toString(), SyncEntityType.BUDGET))
            }
            SyncOperationType.UPDATE -> {
                val serverId = op.serverId?.toIntOrNull() ?: return
                val input = json.decodeFromString<UpdateBudgetInput>(op.data)
                budgetService.update(serverId, input)
                applied.add(SyncApplied(op.localId, serverId.toString(), SyncEntityType.BUDGET))
            }
            SyncOperationType.DELETE -> {
                val serverId = op.serverId?.toIntOrNull() ?: return
                budgetService.delete(serverId)
                applied.add(SyncApplied(op.localId, serverId.toString(), SyncEntityType.BUDGET))
            }
        }
    }
}
