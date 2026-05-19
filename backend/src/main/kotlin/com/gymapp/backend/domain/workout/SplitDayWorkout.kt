package com.gymapp.backend.domain.workout

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "split_day_workouts")
data class SplitDayWorkout(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "split_day_id", nullable = false)
    val splitDayId: UUID,

    @Column(name = "workout_id_api", nullable = false)
    val workoutIdApi: String,

    @Column(name = "exercise_name", nullable = false)
    val exerciseName: String,

    @Column(name = "muscle_group")
    val muscleGroup: String? = null,

    @Column(name = "disp_order", nullable = false)
    val dispOrder: Int = 0,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)