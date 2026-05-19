package com.gymapp.backend.domain.workout

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "workout_logs")
data class WorkoutLog(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "u_id", nullable = false)
    val userId: UUID,

    @Column(name = "split_day_id", nullable = false)
    val splitDayId: UUID,

    @Column(name = "performed_on", nullable = false)
    val performedOn: LocalDate,

    @Column(name = "duration_min")
    val durationMin: Int? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)