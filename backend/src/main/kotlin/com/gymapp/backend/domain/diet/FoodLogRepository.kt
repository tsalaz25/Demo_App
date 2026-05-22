package com.gymapp.backend.domain.diet

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface FoodLogRepository : JpaRepository<FoodLog, UUID> {
    fun findByMealId(mealId: UUID): List<FoodLog>
    fun findByUserIdAndLogDate(userId: UUID, logDate: LocalDate): List<FoodLog>

    @Query("""
        SELECT COALESCE(SUM(f.calories), 0) FROM FoodLog f 
        WHERE f.userId = :userId AND f.logDate = :date
    """)
    fun sumCaloriesByUserIdAndDate(userId: UUID, date: LocalDate): Float

    @Query("""
        SELECT COALESCE(SUM(f.proteinGrams), 0) FROM FoodLog f 
        WHERE f.userId = :userId AND f.logDate = :date
    """)
    fun sumProteinByUserIdAndDate(userId: UUID, date: LocalDate): Float

    @Query("""
        SELECT COALESCE(SUM(f.carbGrams), 0) FROM FoodLog f 
        WHERE f.userId = :userId AND f.logDate = :date
    """)
    fun sumCarbsByUserIdAndDate(userId: UUID, date: LocalDate): Float

    @Query("""
        SELECT COALESCE(SUM(f.fatGrams), 0) FROM FoodLog f 
        WHERE f.userId = :userId AND f.logDate = :date
    """)
    fun sumFatByUserIdAndDate(userId: UUID, date: LocalDate): Float
}