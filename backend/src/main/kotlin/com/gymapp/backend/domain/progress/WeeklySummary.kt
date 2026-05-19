package com.gymapp.backend.domain.progress

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "weekly_summaries")
data class WeeklySummary(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "u_id", nullable = false)
    val userId: UUID,

    @Column(name = "week_start", nullable = false)
    val weekStart: LocalDate,

    @Column(name = "week_end", nullable = false)
    val weekEnd: LocalDate,

    @Column(name = "avg_cals")
    val avgCals: Float? = null,

    @Column(name = "workouts_completed", nullable = false)
    val workoutsCompleted: Int = 0,

    @Column(name = "weight_diff")
    val weightDiff: Float? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)