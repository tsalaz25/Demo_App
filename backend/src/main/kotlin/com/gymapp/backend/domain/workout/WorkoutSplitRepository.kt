package com.gymapp.backend.domain.workout

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface WorkoutSplitRepository : JpaRepository<WorkoutSplit, UUID> {
    fun findByUserId(userId: UUID): List<WorkoutSplit>
    fun findByUserIdAndIsActive(userId: UUID, isActive: Boolean): List<WorkoutSplit>
}