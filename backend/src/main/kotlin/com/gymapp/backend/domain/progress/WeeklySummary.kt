package com.gymapp.backend.domain.progress

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "weekly_summaries")
class WeeklySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    var id: UUID? = null

    @Column(name = "u_id", nullable = false)
    var userId: UUID = UUID.randomUUID()

    @Column(name = "week_start", nullable = false)
    var weekStart: LocalDate = LocalDate.now()

    @Column(name = "week_end", nullable = false)
    var weekEnd: LocalDate = LocalDate.now()

    @Column(name = "avg_cals")
    var avgCals: Float? = null

    @Column(name = "workouts_completed", nullable = false)
    var workoutsCompleted: Int = 0

    @Column(name = "weight_diff")
    var weightDiff: Float? = null

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    constructor()

    constructor(
        userId: UUID, weekStart: LocalDate, weekEnd: LocalDate,
        avgCals: Float?, workoutsCompleted: Int, weightDiff: Float?
    ) {
        this.userId = userId
        this.weekStart = weekStart
        this.weekEnd = weekEnd
        this.avgCals = avgCals
        this.workoutsCompleted = workoutsCompleted
        this.weightDiff = weightDiff
    }
}