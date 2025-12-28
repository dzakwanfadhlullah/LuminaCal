package com.example.luminacal.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.luminacal.model.ActivityLevel
import com.example.luminacal.model.FitnessGoal
import com.example.luminacal.model.Gender
import com.example.luminacal.model.HealthMetrics

@Entity(tableName = "health_metrics")
data class HealthMetricsEntity(
    @PrimaryKey val id: Int = 1, // Single row, always update
    val userName: String = "User",
    val weight: Float,
    val height: Float,
    val age: Int,
    val gender: String,
    val activityLevel: String,
    val fitnessGoal: String,
    val waterTargetMl: Int = 2000,
    val lastUpdated: Long
) {
    fun toHealthMetrics(): HealthMetrics = HealthMetrics(
        userName = userName,
        weight = weight,
        height = height,
        age = age,
        gender = Gender.valueOf(gender),
        activityLevel = ActivityLevel.valueOf(activityLevel),
        fitnessGoal = FitnessGoal.valueOf(fitnessGoal),
        waterTargetMl = waterTargetMl
    )

    companion object {
        fun fromHealthMetrics(metrics: HealthMetrics): HealthMetricsEntity =
            HealthMetricsEntity(
                id = 1,
                userName = metrics.userName,
                weight = metrics.weight,
                height = metrics.height,
                age = metrics.age,
                gender = metrics.gender.name,
                activityLevel = metrics.activityLevel.name,
                fitnessGoal = metrics.fitnessGoal.name,
                waterTargetMl = metrics.waterTargetMl,
                lastUpdated = System.currentTimeMillis()
            )
    }
}
