package com.gymapp.backend.domain.workout

import java.time.LocalDate
import java.util.UUID

// --- Split DTOs ---
data class CreateSplitRequest(
    val splitName: String,
    val description: String?
)

data class SplitResponse(
    val id: UUID,
    val splitName: String,
    val description: String?,
    val isActive: Boolean,
    val days: List<SplitDayResponse> = emptyList()
)

// --- SplitDay DTOs ---
data class CreateSplitDayRequest(
    val splitId: UUID,
    val onDay: String,
    val workoutName: String
)

data class SplitDayResponse(
    val id: UUID,
    val onDay: String,
    val workoutName: String,
    val exercises: List<SplitDayWorkoutResponse> = emptyList()
)

// --- SplitDayWorkout DTOs ---
data class AddExerciseRequest(
    val splitDayId: UUID,
    val workoutIdApi: String,
    val exerciseName: String,
    val muscleGroup: String?,
    val dispOrder: Int
)

data class SplitDayWorkoutResponse(
    val id: UUID,
    val workoutIdApi: String,
    val exerciseName: String,
    val muscleGroup: String?,
    val dispOrder: Int
)

// --- WorkoutLog DTOs ---
data class CreateWorkoutLogRequest(
    val splitDayId: UUID,
    val performedOn: LocalDate,
    val durationMin: Int?
)

data class WorkoutLogResponse(
    val id: UUID,
    val splitDayId: UUID,
    val performedOn: LocalDate,
    val durationMin: Int?,
    val sets: List<WorkoutPerformedResponse> = emptyList()
)

// --- WorkoutPerformed DTOs ---
data class LogSetRequest(
    val workoutLogId: UUID,
    val workoutIdApi: String,
    val setNumber: Int,
    val reps: Int,
    val weightForSetLbs: Float,
    val completed: Boolean
)

data class WorkoutPerformedResponse(
    val id: UUID,
    val workoutIdApi: String,
    val setNumber: Int,
    val reps: Int,
    val weightForSetLbs: Float,
    val completed: Boolean
)

// --- ExerciseDB DTOs ---
data class ExerciseResult(
    val exerciseId: String,
    val name: String,
    val bodyParts: List<String>,
    val equipments: List<String>,
    val targetMuscles: List<String>,
    val imageUrl: String?
)