package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales_channels")
data class ChannelEntity(
    @PrimaryKey
    val code: String,               // "DINE_IN", "TAKEOUT", "DOORDASH", "UBER_EATS", "SKIP_THE_DISHES", "BANQUET"
    val name: String,               // Display name
    val defaultCommissionRate: Double = 0.0, // e.g. 0.20 for 20%
    val receivableAccountCode: String,       // e.g. "1060" for DoorDash Receivable
    val commissionExpenseAccountCode: String = "6110", // Platform Commission Expense
    val isActive: Boolean = true
)
