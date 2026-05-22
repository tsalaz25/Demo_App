package com.gymapp.backend.domain.goal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface GoalRepository : JpaRepository<Goal, UUID> {
    fun findByUserId(userId: UUID): List<Goal>
    fun findFirstByUserIdAndStatusOrderByCreatedAtDesc(
        userId: UUID, status: String
    ): Goal?
}