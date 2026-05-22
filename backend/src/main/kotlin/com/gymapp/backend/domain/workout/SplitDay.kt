package com.gymapp.backend.domain.workout

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "split_days")
class SplitDay {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    var id: UUID? = null

    @Column(name = "split_id", nullable = false)
    var splitId: UUID = UUID.randomUUID()

    @Column(name = "on_day", nullable = false)
    var onDay: String = ""

    @Column(name = "workout_name", nullable = false)
    var workoutName: String = ""

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    constructor()

    constructor(splitId: UUID, onDay: String, workoutName: String) {
        this.splitId = splitId
        this.onDay = onDay
        this.workoutName = workoutName
    }
}