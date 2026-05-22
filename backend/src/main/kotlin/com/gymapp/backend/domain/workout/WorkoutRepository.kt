package com.gymapp.backend.domain.workout

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface SplitDayRepository : JpaRepository<SplitDay, UUID> {
    fun findBySplitId(splitId: UUID): List<SplitDay>
}

@Repository
interface SplitDayWorkoutRepository : JpaRepository<SplitDayWorkout, UUID> {
    fun findBySplitDayId(splitDayId: UUID): List<SplitDayWorkout>
}

@Repository
interface WorkoutLogRepository : JpaRepository<WorkoutLog, UUID> {
    fun findByUserIdAndPerformedOn(userId: UUID, performedOn: LocalDate): List<WorkoutLog>
    fun findByUserId(userId: UUID): List<WorkoutLog>
    fun findByUserIdAndSplitDayId(userId: UUID, splitDayId: UUID): List<WorkoutLog>
}

@Repository
interface WorkoutPerformedRepository : JpaRepository<WorkoutPerformed, UUID> {
    fun findByWorkoutLogId(workoutLogId: UUID): List<WorkoutPerformed>
}