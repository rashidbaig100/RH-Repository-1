package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AccountType
import com.example.data.model.NormalBalance

@Entity(tableName = "chart_of_accounts")
data class AccountEntity(
    @PrimaryKey
    val code: String,                     // e.g., "1010", "4010"
    val name: String,                     // e.g., "Operating Bank Account", "Dine-In Food Sales"
    val type: AccountType,                // ASSET, LIABILITY, EQUITY, REVENUE, COGS, EXPENSE
    val normalBalance: NormalBalance,     // DEBIT or CREDIT
    val category: String,                 // e.g., "Current Assets", "Operating Revenue", "Direct Food Cost"
    val description: String = "",
    val isSystemAccount: Boolean = false, // Critical accounts that cannot be deleted
    val isActive: Boolean = true,
    val currentBalance: Double = 0.0      // Current cached balance in CAD
)
