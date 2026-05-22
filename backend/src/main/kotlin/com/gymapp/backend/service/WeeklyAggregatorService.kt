package com.gymapp.backend.service

import com.gymapp.backend.domain.diet.FoodLogRepository
import com.gymapp.backend.domain.progress.WeeklySummary
import com.gymapp.backend.domain.progress.WeeklySummaryRepository
import com.gymapp.backend.domain.progress.WeightLogRepository
import com.gymapp.backend.domain.user.UserRepository
import com.gymapp.backend.domain.workout.WorkoutLogRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Service
class WeeklyAggregatorService(
    private val userRepository: UserRepository,
    private val foodLogRepository: FoodLogRepository,
    private val workoutLogRepository: WorkoutLogRepository,
    private val weightLogRepository: WeightLogRepository,
    private val weeklySummaryRepository: WeeklySummaryRepository
) {
    // Runs every Monday at 6am
    @Scheduled(cron = "0 0 6 * * MON")
    fun aggregateLastWeek() {
        val today = LocalDate.now()
        val weekStart = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weekEnd = weekStart.plusDays(6)

        val users = userRepository.findAll()
        for (user in users) {
            val userId = user.id ?: continue

            // Average daily calories for the week
            val foodLogs = foodLogRepository.findByUserIdAndLogDate(userId, weekStart)
            val allLogs = (0..6).flatMap { dayOffset ->
                foodLogRepository.findByUserIdAndLogDate(userId, weekStart.plusDays(dayOffset.toLong()))
            }
            val avgCals = if (allLogs.isNotEmpty())
                allLogs.sumOf { it.calories.toDouble() }.toFloat() / 7f
            else null

            // Workouts completed
            val workoutsCompleted = (0..6).sumOf { dayOffset ->
                workoutLogRepository.findByUserIdAndPerformedOn(
                    userId, weekStart.plusDays(dayOffset.toLong())
                ).size
            }

            // Weight diff (last weight of week minus first)
            val weightLogs = weightLogRepository.findByUserIdAndOnDateBetweenOrderByOnDateAsc(
                userId, weekStart, weekEnd
            )
            val weightDiff = if (weightLogs.size >= 2)
                weightLogs.last().weightLbs - weightLogs.first().weightLbs
            else null

            // Upsert — update if exists, create if not
            val existing = weeklySummaryRepository.findByUserIdAndWeekStart(userId, weekStart)
            if (existing != null) {
                existing.avgCals = avgCals
                existing.workoutsCompleted = workoutsCompleted
                existing.weightDiff = weightDiff
                weeklySummaryRepository.save(existing)
            } else {
                weeklySummaryRepository.save(
                    WeeklySummary(userId, weekStart, weekEnd, avgCals, workoutsCompleted, weightDiff)
                )
            }
        }
    }
}