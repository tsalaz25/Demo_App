package com.gymapp.backend.domain.diet

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "food_logs")
class FoodLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    var id: UUID? = null

    @Column(name = "u_id", nullable = false)
    var userId: UUID = UUID.randomUUID()

    @Column(name = "meal_id", nullable = false)
    var mealId: UUID = UUID.randomUUID()

    @Column(name = "food_id_api", nullable = false)
    var foodIdApi: String = ""

    @Column(name = "food_name", nullable = false)
    var foodName: String = ""

    @Column(name = "serving_quantity", nullable = false)
    var servingQuantity: Float = 0f

    @Column(nullable = false)
    var calories: Float = 0f

    @Column(name = "protein_grams", nullable = false)
    var proteinGrams: Float = 0f

    @Column(name = "carb_grams", nullable = false)
    var carbGrams: Float = 0f

    @Column(name = "fat_grams", nullable = false)
    var fatGrams: Float = 0f

    @Column(name = "log_date", nullable = false)
    var logDate: LocalDate = LocalDate.now()

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    constructor()

    constructor(
        userId: UUID, mealId: UUID, foodIdApi: String, foodName: String,
        servingQuantity: Float, calories: Float, proteinGrams: Float,
        carbGrams: Float, fatGrams: Float, logDate: LocalDate
    ) {
        this.userId = userId
        this.mealId = mealId
        this.foodIdApi = foodIdApi
        this.foodName = foodName
        this.servingQuantity = servingQuantity
        this.calories = calories
        this.proteinGrams = proteinGrams
        this.carbGrams = carbGrams
        this.fatGrams = fatGrams
        this.logDate = logDate
    }
}