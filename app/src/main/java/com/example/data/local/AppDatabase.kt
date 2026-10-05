package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AccountDao
import com.example.data.local.dao.AuditDao
import com.example.data.local.dao.BranchDao
import com.example.data.local.dao.ChannelDao
import com.example.data.local.dao.JournalDao
import com.example.data.local.dao.PeriodLockDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.BranchEntity
import com.example.data.local.entity.ChannelEntity
import com.example.data.local.entity.JournalEntryEntity
import com.example.data.local.entity.JournalLineEntity
import com.example.data.local.entity.PeriodLockEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AccountEntity::class,
        BranchEntity::class,
        UserEntity::class,
        JournalEntryEntity::class,
        JournalLineEntity::class,
        AuditLogEntity::class,
        ChannelEntity::class,
        PeriodLockEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun branchDao(): BranchDao
    abstract fun userDao(): UserDao
    abstract fun journalDao(): JournalDao
    abstract fun auditDao(): AuditDao
    abstract fun channelDao(): ChannelDao
    abstract fun periodLockDao(): PeriodLockDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "eastern_flavours_finance.db"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(database: AppDatabase) {
            database.accountDao().insertAccounts(SeedData.defaultAccounts)
            database.branchDao().insertBranches(SeedData.defaultBranches)
            database.userDao().insertUsers(SeedData.defaultUsers)
            database.channelDao().insertChannels(SeedData.defaultChannels)

            database.auditDao().insertLog(
                AuditLogEntity(
                    userId = 1,
                    userName = "System Initialization",
                    userRole = "OWNER",
                    action = "INITIALIZE_DATABASE",
                    entityType = "CHART_OF_ACCOUNTS",
                    entityIdentifier = "SYSTEM",
                    details = "Initialized standard Eastern Flavours Canadian restaurant Chart of Accounts, branches, and delivery channels."
                )
            )
        }
    }
}
