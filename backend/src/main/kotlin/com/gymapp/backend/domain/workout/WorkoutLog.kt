package com.gymapp.backend.domain.workout

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "workout_logs")
class WorkoutLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    var id: UUID? = null

    @Column(name = "u_id", nullable = false)
    var userId: UUID = UUID.randomUUID()

    @Column(name = "split_day_id", nullable = false)
    var splitDayId: UUID = UUID.randomUUID()

    @Column(name = "performed_on", nullable = false)
    var performedOn: LocalDate = LocalDate.now()

    @Column(name = "duration_min")
    var durationMin: Int? = null

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    constructor()

    constructor(userId: UUID, splitDayId: UUID, performedOn: LocalDate, durationMin: Int?) {
        this.userId = userId
        this.splitDayId = splitDayId
        this.performedOn = performedOn
        this.durationMin = durationMin
    }
}