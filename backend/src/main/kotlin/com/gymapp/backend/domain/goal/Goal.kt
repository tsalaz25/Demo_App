package com.gymapp.backend.domain.goal

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "goals")
class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    var id: UUID? = null

    @Column(name = "u_id", nullable = false)
    var userId: UUID = UUID.randomUUID()

    @Column(name = "target_weight_lbs", nullable = false)
    var targetWeightLbs: Float = 0f

    @Column(name = "start_weight_lbs", nullable = false)
    var startWeightLbs: Float = 0f

    @Column(name = "target_daily_cal_intake", nullable = false)
    var targetDailyCalIntake: Int = 0

    @Column(nullable = false)
    var status: String = "active"

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate = LocalDate.now()

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    constructor()

    constructor(
        userId: UUID, targetWeightLbs: Float, startWeightLbs: Float,
        targetDailyCalIntake: Int, status: String, startDate: LocalDate
    ) {
        this.userId = userId
        this.targetWeightLbs = targetWeightLbs
        this.startWeightLbs = startWeightLbs
        this.targetDailyCalIntake = targetDailyCalIntake
        this.status = status
        this.startDate = startDate
    }
}