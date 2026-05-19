package com.gymapp.backend.domain.goal

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "goals")
data class Goal(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "u_id", nullable = false)
    val userId: UUID,

    @Column(name = "target_weight_lbs", nullable = false)
    val targetWeightLbs: Float,

    @Column(name = "start_weight_lbs", nullable = false)
    val startWeightLbs: Float,

    @Column(name = "target_daily_cal_intake", nullable = false)
    val targetDailyCalIntake: Int,

    @Column(nullable = false)
    val status: String = "active",

    @Column(name = "start_date", nullable = false)
    val startDate: LocalDate,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
