package com.gymapp.backend.domain.workout

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "workouts_performed")
class WorkoutPerformed {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    var id: UUID? = null

    @Column(name = "workout_log_id", nullable = false)
    var workoutLogId: UUID = UUID.randomUUID()

    @Column(name = "workout_id_api", nullable = false)
    var workoutIdApi: String = ""

    @Column(name = "set_number", nullable = false)
    var setNumber: Int = 0

    @Column(nullable = false)
    var reps: Int = 0

    @Column(name = "weight_for_set_lbs", nullable = false)
    var weightForSetLbs: Float = 0f

    @Column(nullable = false)
    var completed: Boolean = false

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    constructor()

    constructor(
        workoutLogId: UUID, workoutIdApi: String, setNumber: Int,
        reps: Int, weightForSetLbs: Float, completed: Boolean
    ) {
        this.workoutLogId = workoutLogId
        this.workoutIdApi = workoutIdApi
        this.setNumber = setNumber
        this.reps = reps
        this.weightForSetLbs = weightForSetLbs
        this.completed = completed
    }
}