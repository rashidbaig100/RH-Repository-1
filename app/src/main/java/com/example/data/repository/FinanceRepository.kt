package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.SeedData
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.BranchEntity
import com.example.data.local.entity.ChannelEntity
import com.example.data.local.entity.JournalEntryEntity
import com.example.data.local.entity.JournalLineEntity
import com.example.data.local.entity.PeriodLockEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.AccountType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FinanceRepository(private val database: AppDatabase) {

    private val accountDao = database.accountDao()
    private val branchDao = database.branchDao()
    private val userDao = database.userDao()
    private val journalDao = database.journalDao()
    private val auditDao = database.auditDao()
    private val channelDao = database.channelDao()
    private val periodLockDao = database.periodLockDao()

    // Accounts
    val allAccounts: Flow<List<AccountEntity>> = accountDao.getAllAccounts()
    fun getAccountsByType(type: AccountType): Flow<List<AccountEntity>> = accountDao.getAccountsByType(type)
    fun searchAccounts(query: String): Flow<List<AccountEntity>> = accountDao.searchAccounts(query)

    suspend fun addAccount(account: AccountEntity, performedByUser: UserEntity) = withContext(Dispatchers.IO) {
        accountDao.insertAccount(account)
        auditDao.insertLog(
            AuditLogEntity(
                userId = performedByUser.id,
                userName = performedByUser.name,
                userRole = performedByUser.role.name,
                action = "CREATE_ACCOUNT",
                entityType = "CHART_OF_ACCOUNTS",
                entityIdentifier = account.code,
                details = "Created ${account.type.displayName} account: ${account.code} - ${account.name} (${account.normalBalance.name})",
                branchId = performedByUser.branchId
            )
        )
    }

    suspend fun deleteCustomAccount(code: String, performedByUser: UserEntity): Boolean = withContext(Dispatchers.IO) {
        val deleted = accountDao.deleteCustomAccount(code)
        if (deleted > 0) {
            auditDao.insertLog(
                AuditLogEntity(
                    userId = performedByUser.id,
                    userName = performedByUser.name,
                    userRole = performedByUser.role.name,
                    action = "DELETE_ACCOUNT",
                    entityType = "CHART_OF_ACCOUNTS",
                    entityIdentifier = code,
                    details = "Deleted custom account: $code",
                    branchId = performedByUser.branchId
                )
            )
            true
        } else {
            false
        }
    }

    // Branches
    val activeBranches: Flow<List<BranchEntity>> = branchDao.getAllActiveBranches()
    suspend fun addBranch(branch: BranchEntity, performedByUser: UserEntity): Long = withContext(Dispatchers.IO) {
        val id = branchDao.insertBranch(branch)
        auditDao.insertLog(
            AuditLogEntity(
                userId = performedByUser.id,
                userName = performedByUser.name,
                userRole = performedByUser.role.name,
                action = "CREATE_BRANCH",
                entityType = "BRANCH",
                entityIdentifier = branch.code,
                details = "Added branch location: ${branch.name}",
                branchId = id
            )
        )
        id
    }

    // Users & Authentication
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun authenticatePin(pin: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.authenticateByPin(pin)
    }

    // Channels
    val allChannels: Flow<List<ChannelEntity>> = channelDao.getAllChannels()

    // Audit Logs
    val recentAuditLogs: Flow<List<AuditLogEntity>> = auditDao.getRecentAuditLogs()

    suspend fun logAction(
        user: UserEntity,
        action: String,
        entityType: String,
        identifier: String,
        details: String,
        branchId: Long? = null
    ) = withContext(Dispatchers.IO) {
        auditDao.insertLog(
            AuditLogEntity(
                userId = user.id,
                userName = user.name,
                userRole = user.role.name,
                action = action,
                entityType = entityType,
                entityIdentifier = identifier,
                details = details,
                branchId = branchId ?: user.branchId
            )
        )
    }

    // Period Lock
    val periodLocks: Flow<List<PeriodLockEntity>> = periodLockDao.getAllPeriodLocks()

    suspend fun isPeriodLocked(yearMonth: String): Boolean = withContext(Dispatchers.IO) {
        periodLockDao.getLockForPeriod(yearMonth)?.isLocked ?: false
    }

    suspend fun setPeriodLock(yearMonth: String, isLocked: Boolean, user: UserEntity, notes: String = "") = withContext(Dispatchers.IO) {
        periodLockDao.insertOrUpdateLock(
            PeriodLockEntity(
                yearMonth = yearMonth,
                isLocked = isLocked,
                lockedByUserId = user.id,
                lockedByUserName = user.name,
                lockedAt = System.currentTimeMillis(),
                notes = notes
            )
        )
        auditDao.insertLog(
            AuditLogEntity(
                userId = user.id,
                userName = user.name,
                userRole = user.role.name,
                action = if (isLocked) "LOCK_PERIOD" else "UNLOCK_PERIOD",
                entityType = "PERIOD_LOCK",
                entityIdentifier = yearMonth,
                details = "${if (isLocked) "Locked" else "Unlocked"} accounting period $yearMonth. Notes: $notes",
                branchId = user.branchId
            )
        )
    }

    // Seed verification (ensures initial seed data is loaded even if SQLite callback didn't trigger)
    suspend fun ensureDataSeeded() = withContext(Dispatchers.IO) {
        if (accountDao.getAccountCount() == 0) {
            accountDao.insertAccounts(SeedData.defaultAccounts)
        }
        if (branchDao.getBranchCount() == 0) {
            branchDao.insertBranches(SeedData.defaultBranches)
        }
        if (userDao.getUserCount() == 0) {
            userDao.insertUsers(SeedData.defaultUsers)
        }
    }
}
