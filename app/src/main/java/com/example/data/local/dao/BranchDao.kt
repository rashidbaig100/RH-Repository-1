package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BranchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BranchDao {
    @Query("SELECT * FROM branches WHERE isActive = 1 ORDER BY id ASC")
    fun getAllActiveBranches(): Flow<List<BranchEntity>>

    @Query("SELECT * FROM branches WHERE id = :id LIMIT 1")
    suspend fun getBranchById(id: Long): BranchEntity?

    @Query("SELECT COUNT(*) FROM branches")
    suspend fun getBranchCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: BranchEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranches(branches: List<BranchEntity>)

    @Update
    suspend fun updateBranch(branch: BranchEntity)
}
