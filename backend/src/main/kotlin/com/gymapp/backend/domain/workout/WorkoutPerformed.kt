package com.gymapp.backend.domain.workout

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "workouts_performed")
data class WorkoutPerformed(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "workout_log_id", nullable = false)
    val workoutLogId: UUID,

    @Column(name = "workout_id_api", nullable = false)
    val workoutIdApi: String,

    @Column(name = "set_number", nullable = false)
    val setNumber: Int,

    @Column(nullable = false)
    val reps: Int,

    @Column(name = "weight_for_set_lbs", nullable = false)
    val weightForSetLbs: Float,

    @Column(nullable = false)
    val completed: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)