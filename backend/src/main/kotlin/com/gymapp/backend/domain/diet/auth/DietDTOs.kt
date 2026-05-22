package com.gymapp.backend.domain.diet

import java.time.LocalDate
import java.util.UUID

// --- Meal DTOs ---
data class CreateMealRequest(
    val mealDate: LocalDate,
    val mealName: String
)

data class MealResponse(
    val id: UUID,
    val mealDate: LocalDate,
    val mealName: String,
    val foodLogs: List<FoodLogResponse> = emptyList()
)

// --- FoodLog DTOs ---
data class LogFoodRequest(
    val mealId: UUID,
    val foodIdApi: String,
    val foodName: String,
    val servingQuantity: Float,
    val calories: Float,
    val proteinGrams: Float,
    val carbGrams: Float,
    val fatGrams: Float,
    val logDate: LocalDate
)

data class FoodLogResponse(
    val id: UUID,
    val foodName: String,
    val servingQuantity: Float,
    val calories: Float,
    val proteinGrams: Float,
    val carbGrams: Float,
    val fatGrams: Float
)

// --- Daily Summary DTO ---
data class DailySummaryResponse(
    val date: LocalDate,
    val totalCalories: Float,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
    val meals: List<MealResponse>
)

// --- FatSecret Search DTOs ---
data class FoodSearchResult(
    val foodId: String,
    val foodName: String,
    val brandName: String?,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val servingDescription: String
)