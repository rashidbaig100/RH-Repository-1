package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: Long,
    val userName: String,
    val userRole: String,
    val action: String,             // e.g. "CREATE_ACCOUNT", "POST_JOURNAL", "LOCK_PERIOD", "UPDATE_TARGET"
    val entityType: String,         // "ACCOUNT", "JOURNAL_ENTRY", "PERIOD_LOCK"
    val entityIdentifier: String,   // Account code or entry number
    val details: String,            // Descriptive summary of change
    val branchId: Long? = null
)
