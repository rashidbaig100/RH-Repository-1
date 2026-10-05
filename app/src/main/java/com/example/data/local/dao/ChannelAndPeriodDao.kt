package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ChannelEntity
import com.example.data.local.entity.PeriodLockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Query("SELECT * FROM sales_channels WHERE isActive = 1 ORDER BY code ASC")
    fun getAllChannels(): Flow<List<ChannelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<ChannelEntity>)

    @Update
    suspend fun updateChannel(channel: ChannelEntity)
}

@Dao
interface PeriodLockDao {
    @Query("SELECT * FROM period_locks ORDER BY yearMonth DESC")
    fun getAllPeriodLocks(): Flow<List<PeriodLockEntity>>

    @Query("SELECT * FROM period_locks WHERE yearMonth = :yearMonth LIMIT 1")
    suspend fun getLockForPeriod(yearMonth: String): PeriodLockEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLock(periodLock: PeriodLockEntity)
}
