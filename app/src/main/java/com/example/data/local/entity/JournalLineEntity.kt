package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "journal_lines",
    foreignKeys = [
        ForeignKey(
            entity = JournalEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["journalEntryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["code"],
            childColumns = ["accountCode"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("journalEntryId"),
        Index("accountCode")
    ]
)
data class JournalLineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val journalEntryId: Long,
    val accountCode: String,
    val debitAmount: Double = 0.0,
    val creditAmount: Double = 0.0,
    val memo: String = ""
)
