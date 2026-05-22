package com.gymapp.backend.domain.workout

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "workout_splits")
class WorkoutSplit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    var id: UUID? = null

    @Column(name = "u_id", nullable = false)
    var userId: UUID = UUID.randomUUID()

    @Column(name = "split_name", nullable = false)
    var splitName: String = ""

    var description: String? = null

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    constructor()

    constructor(userId: UUID, splitName: String, description: String?) {
        this.userId = userId
        this.splitName = splitName
        this.description = description
    }
}