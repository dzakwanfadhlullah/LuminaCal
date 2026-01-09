package com.example.luminacal.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity to store history of scanned foods for quick re-logging
 */
@Entity(
    tableName = "scan_history",
    indices = [Index(value = ["date"])]
)
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val foodName: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val imageUrl: String? = null,
    val servingSize: String = "1 serving",
    val date: Long = System.currentTimeMillis()
)
