package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.EntrySource
import com.example.data.model.JournalStatus

@Entity(
    tableName = "journal_entries",
    foreignKeys = [
        ForeignKey(
            entity = BranchEntity::class,
            parentColumns = ["id"],
            childColumns = ["branchId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("branchId"),
        Index("date"),
        Index("status")
    ]
)
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entryNumber: String,        // e.g. "JE-2026-0001"
    val branchId: Long,
    val date: Long,                 // Epoch millis
    val source: EntrySource,        // SALE, PURCHASE, EXPENSE, PAYROLL, etc.
    val referenceNumber: String = "", // Invoice #, POS bill #, Receipt #
    val description: String,
    val totalAmount: Double,        // Balanced sum of debits
    val status: JournalStatus = JournalStatus.POSTED,
    val postedByUserId: Long,
    val postedAt: Long = System.currentTimeMillis(),
    val isPeriodLocked: Boolean = false,
    val notes: String = ""
)
