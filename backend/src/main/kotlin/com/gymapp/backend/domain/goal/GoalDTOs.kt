package com.gymapp.backend.domain.goal

import java.time.LocalDate
import java.util.UUID

data class CreateGoalRequest(
    val targetWeightLbs: Float,
    val startWeightLbs: Float,
    val targetDailyCalIntake: Int,
    val status: String = "active",
    val startDate: LocalDate
)

data class GoalResponse(
    val id: UUID,
    val targetWeightLbs: Float,
    val startWeightLbs: Float,
    val targetDailyCalIntake: Int,
    val status: String,
    val startDate: LocalDate
)