package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.local.entity.JournalEntryEntity
import com.example.data.local.entity.JournalLineEntity
import kotlinx.coroutines.flow.Flow

data class JournalEntryWithLines(
    val entry: JournalEntryEntity,
    val lines: List<JournalLineEntity>
)

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY date DESC, id DESC")
    fun getAllEntries(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE branchId = :branchId ORDER BY date DESC, id DESC")
    fun getEntriesByBranch(branchId: Long): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: Long): JournalEntryEntity?

    @Query("SELECT * FROM journal_lines WHERE journalEntryId = :entryId")
    suspend fun getLinesForEntry(entryId: Long): List<JournalLineEntity>

    @Query("SELECT * FROM journal_lines WHERE accountCode = :accountCode")
    suspend fun getLinesForAccount(accountCode: String): List<JournalLineEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertEntry(entry: JournalEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLines(lines: List<JournalLineEntity>)

    @Query("SELECT COUNT(*) FROM journal_entries")
    suspend fun getEntryCount(): Int

    @Query("SELECT COALESCE(SUM(totalAmount), 0.0) FROM journal_entries WHERE status = 'POSTED'")
    fun getTotalVolume(): Flow<Double>
}
