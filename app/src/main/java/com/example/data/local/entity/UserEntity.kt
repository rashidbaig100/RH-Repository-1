package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val role: UserRole,
    val pinCode: String,            // 4-digit PIN for instant terminal access
    val branchId: Long? = null,     // Assigned branch or null for all branches (Owner)
    val isActive: Boolean = true
)
