package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "period_locks")
data class PeriodLockEntity(
    @PrimaryKey
    val yearMonth: String,          // e.g. "2026-09"
    val isLocked: Boolean = true,
    val lockedByUserId: Long,
    val lockedByUserName: String,
    val lockedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)
