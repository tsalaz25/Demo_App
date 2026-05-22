package com.gymapp.backend.domain.progress

import java.time.LocalDate
import java.util.UUID

// --- WeightLog DTOs ---
data class LogWeightRequest(
    val weightLbs: Float,
    val onDate: LocalDate,
    val notes: String?
)

data class WeightLogResponse(
    val id: UUID,
    val weightLbs: Float,
    val onDate: LocalDate,
    val notes: String?
)

// --- WeeklySummary DTOs ---
data class WeeklySummaryResponse(
    val id: UUID,
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val avgCals: Float?,
    val workoutsCompleted: Int,
    val weightDiff: Float?
)