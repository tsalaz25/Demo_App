package com.gymapp.backend.domain.progress

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "weight_logs")
data class WeightLog(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "u_id", nullable = false)
    val userId: UUID,

    @Column(name = "weight_lbs", nullable = false)
    val weightLbs: Float,

    @Column(name = "on_date", nullable = false)
    val onDate: LocalDate,

    val notes: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)