package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AccountEntity
import com.example.data.model.AccountType
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM chart_of_accounts ORDER BY code ASC")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM chart_of_accounts WHERE type = :type ORDER BY code ASC")
    fun getAccountsByType(type: AccountType): Flow<List<AccountEntity>>

    @Query("SELECT * FROM chart_of_accounts WHERE code = :code LIMIT 1")
    suspend fun getAccountByCode(code: String): AccountEntity?

    @Query("SELECT * FROM chart_of_accounts WHERE code LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%' ORDER BY code ASC")
    fun searchAccounts(query: String): Flow<List<AccountEntity>>

    @Query("SELECT COUNT(*) FROM chart_of_accounts")
    suspend fun getAccountCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<AccountEntity>)

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Query("UPDATE chart_of_accounts SET currentBalance = :balance WHERE code = :code")
    suspend fun updateAccountBalance(code: String, balance: Double)

    @Query("DELETE FROM chart_of_accounts WHERE code = :code AND isSystemAccount = 0")
    suspend fun deleteCustomAccount(code: String): Int
}
