package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AccountType
import com.example.data.model.EntrySource
import com.example.data.model.JournalStatus
import com.example.data.model.NormalBalance
import com.example.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromAccountType(value: AccountType?): String? = value?.name

    @TypeConverter
    fun toAccountType(value: String?): AccountType? =
        value?.let { runCatching { AccountType.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun fromNormalBalance(value: NormalBalance?): String? = value?.name

    @TypeConverter
    fun toNormalBalance(value: String?): NormalBalance? =
        value?.let { runCatching { NormalBalance.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun fromUserRole(value: UserRole?): String? = value?.name

    @TypeConverter
    fun toUserRole(value: String?): UserRole? =
        value?.let { runCatching { UserRole.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun fromJournalStatus(value: JournalStatus?): String? = value?.name

    @TypeConverter
    fun toJournalStatus(value: String?): JournalStatus? =
        value?.let { runCatching { JournalStatus.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun fromEntrySource(value: EntrySource?): String? = value?.name

    @TypeConverter
    fun toEntrySource(value: String?): EntrySource? =
        value?.let { runCatching { EntrySource.valueOf(it) }.getOrNull() }
}
