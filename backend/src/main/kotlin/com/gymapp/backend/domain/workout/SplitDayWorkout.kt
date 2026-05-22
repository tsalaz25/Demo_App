package com.gymapp.backend.domain.workout

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "split_day_workouts")
class SplitDayWorkout {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    var id: UUID? = null

    @Column(name = "split_day_id", nullable = false)
    var splitDayId: UUID = UUID.randomUUID()

    @Column(name = "workout_id_api", nullable = false)
    var workoutIdApi: String = ""

    @Column(name = "exercise_name", nullable = false)
    var exerciseName: String = ""

    @Column(name = "muscle_group")
    var muscleGroup: String? = null

    @Column(name = "disp_order", nullable = false)
    var dispOrder: Int = 0

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    constructor()

    constructor(
        splitDayId: UUID, workoutIdApi: String, exerciseName: String,
        muscleGroup: String?, dispOrder: Int
    ) {
        this.splitDayId = splitDayId
        this.workoutIdApi = workoutIdApi
        this.exerciseName = exerciseName
        this.muscleGroup = muscleGroup
        this.dispOrder = dispOrder
    }
}