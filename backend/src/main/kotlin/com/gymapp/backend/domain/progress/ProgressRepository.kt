package com.gymapp.backend.domain.progress

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface WeightLogRepository : JpaRepository<WeightLog, UUID> {
    fun findByUserIdOrderByOnDateDesc(userId: UUID): List<WeightLog>
    fun findFirstByUserIdOrderByOnDateDesc(userId: UUID): WeightLog?
    fun findByUserIdAndOnDateBetweenOrderByOnDateAsc(
        userId: UUID, start: LocalDate, end: LocalDate
    ): List<WeightLog>
}

@Repository
interface WeeklySummaryRepository : JpaRepository<WeeklySummary, UUID> {
    fun findByUserIdOrderByWeekStartDesc(userId: UUID): List<WeeklySummary>
    fun findByUserIdAndWeekStart(userId: UUID, weekStart: LocalDate): WeeklySummary?
}