package com.gymapp.backend.domain.diet

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "food_logs")
data class FoodLog(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "u_id", nullable = false)
    val userId: UUID,

    @Column(name = "meal_id", nullable = false)
    val mealId: UUID,

    @Column(name = "food_id_api", nullable = false)
    val foodIdApi: String,

    @Column(name = "food_name", nullable = false)
    val foodName: String,

    @Column(name = "serving_quantity", nullable = false)
    val servingQuantity: Float,

    @Column(nullable = false)
    val calories: Float,

    @Column(name = "protein_grams", nullable = false)
    val proteinGrams: Float,

    @Column(name = "carb_grams", nullable = false)
    val carbGrams: Float,

    @Column(name = "fat_grams", nullable = false)
    val fatGrams: Float,

    @Column(name = "log_date", nullable = false)
    val logDate: LocalDate,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)