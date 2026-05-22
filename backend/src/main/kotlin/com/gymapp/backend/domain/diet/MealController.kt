package com.gymapp.backend.domain.diet

import com.gymapp.backend.auth.JwtService
import com.gymapp.backend.service.FatSecretService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/diet")
class DietController(
    private val mealRepository: MealRepository,
    private val foodLogRepository: FoodLogRepository,
    private val fatSecretService: FatSecretService,
    private val jwtService: JwtService
) {
    // --- Helper to extract user ID from JWT ---
    private fun getUserId(authHeader: String) =
        jwtService.extractUserId(authHeader.substring(7))

    // --- Food Search ---
    @GetMapping("/foods/search")
    fun searchFoods(
        @RequestHeader("Authorization") auth: String,
        @RequestParam query: String
    ): ResponseEntity<List<com.gymapp.backend.domain.diet.FoodSearchResult>> {
        val results = fatSecretService.searchFoods(query)
        return ResponseEntity.ok(results)
    }

    // --- Create Meal ---
    @PostMapping("/meals")
    fun createMeal(
        @RequestHeader("Authorization") auth: String,
        @RequestBody request: CreateMealRequest
    ): ResponseEntity<MealResponse> {
        val userId = getUserId(auth)
        val meal = mealRepository.save(
            Meal(
                userId = userId,
                mealDate = request.mealDate,
                mealName = request.mealName
            )
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(meal.toResponse())
    }

    // --- Get Meals by Date ---
    @GetMapping("/meals")
    fun getMealsByDate(
        @RequestHeader("Authorization") auth: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<List<MealResponse>> {
        val userId = getUserId(auth)
        val meals = mealRepository.findByUserIdAndMealDate(userId, date)
        val responses = meals.map { meal ->
            val logs = foodLogRepository.findByMealId(meal.id!!)
            meal.toResponse(logs)
        }
        return ResponseEntity.ok(responses)
    }

    // --- Log Food ---
    @PostMapping("/food-logs")
    fun logFood(
        @RequestHeader("Authorization") auth: String,
        @RequestBody request: LogFoodRequest
    ): ResponseEntity<FoodLogResponse> {
        val userId = getUserId(auth)
        val log = foodLogRepository.save(
            FoodLog(
                userId = userId,
                mealId = request.mealId,
                foodIdApi = request.foodIdApi,
                foodName = request.foodName,
                servingQuantity = request.servingQuantity,
                calories = request.calories,
                proteinGrams = request.proteinGrams,
                carbGrams = request.carbGrams,
                fatGrams = request.fatGrams,
                logDate = request.logDate
            )
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(log.toResponse())
    }

    // --- Get Daily Summary ---
    @GetMapping("/summary")
    fun getDailySummary(
        @RequestHeader("Authorization") auth: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<DailySummaryResponse> {
        val userId = getUserId(auth)
        val meals = mealRepository.findByUserIdAndMealDate(userId, date)
        val mealsWithLogs = meals.map { meal ->
            meal.toResponse(foodLogRepository.findByMealId(meal.id!!))
        }
        return ResponseEntity.ok(
            DailySummaryResponse(
                date = date,
                totalCalories = foodLogRepository.sumCaloriesByUserIdAndDate(userId, date),
                totalProtein = foodLogRepository.sumProteinByUserIdAndDate(userId, date),
                totalCarbs = foodLogRepository.sumCarbsByUserIdAndDate(userId, date),
                totalFat = foodLogRepository.sumFatByUserIdAndDate(userId, date),
                meals = mealsWithLogs
            )
        )
    }
}

// --- Extension functions to map entities to responses ---
fun Meal.toResponse(logs: List<FoodLog> = emptyList()) = MealResponse(
    id = this.id!!,
    mealDate = this.mealDate,
    mealName = this.mealName,
    foodLogs = logs.map { it.toResponse() }
)

fun FoodLog.toResponse() = FoodLogResponse(
    id = this.id!!,
    foodName = this.foodName,
    servingQuantity = this.servingQuantity,
    calories = this.calories,
    proteinGrams = this.proteinGrams,
    carbGrams = this.carbGrams,
    fatGrams = this.fatGrams
)