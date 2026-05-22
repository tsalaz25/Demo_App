package com.gymapp.backend.domain.progress

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "weight_logs")
class WeightLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    var id: UUID? = null

    @Column(name = "u_id", nullable = false)
    var userId: UUID = UUID.randomUUID()

    @Column(name = "weight_lbs", nullable = false)
    var weightLbs: Float = 0f

    @Column(name = "on_date", nullable = false)
    var onDate: LocalDate = LocalDate.now()

    var notes: String? = null

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    constructor()

    constructor(userId: UUID, weightLbs: Float, onDate: LocalDate, notes: String?) {
        this.userId = userId
        this.weightLbs = weightLbs
        this.onDate = onDate
        this.notes = notes
    }
}