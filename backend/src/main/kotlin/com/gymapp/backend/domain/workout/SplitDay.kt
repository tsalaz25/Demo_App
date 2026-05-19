package com.gymapp.backend.domain.workout

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "split_days")
data class SplitDay(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "split_id", nullable = false)
    val splitId: UUID,

    @Column(name = "on_day", nullable = false)
    val onDay: String,

    @Column(name = "workout_name", nullable = false)
    val workoutName: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)