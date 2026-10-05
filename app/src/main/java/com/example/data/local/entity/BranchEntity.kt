package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "branches")
data class BranchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,               // e.g. "Eastern Flavours - Main Dining & Banquets"
    val code: String,               // e.g. "BR-01"
    val address: String,            // e.g. "124 King Street West, Toronto, ON"
    val phone: String = "",
    val isBanquetEnabled: Boolean = true,
    val isActive: Boolean = true
)
