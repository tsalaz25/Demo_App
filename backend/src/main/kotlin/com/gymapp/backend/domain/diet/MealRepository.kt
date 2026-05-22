package com.gymapp.backend.domain.diet

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface MealRepository : JpaRepository<Meal, UUID> {
    fun findByUserIdAndMealDate(userId: UUID, mealDate: LocalDate): List<Meal>
    fun findByUserId(userId: UUID): List<Meal>
}