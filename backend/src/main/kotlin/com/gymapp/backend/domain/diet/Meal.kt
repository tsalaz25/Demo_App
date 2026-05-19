package com.gymapp.backend.domain.diet

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "meals")
data class Meal(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "u_id", nullable = false)
    val userId: UUID,

    @Column(name = "meal_date", nullable = false)
    val mealDate: LocalDate,

    @Column(name = "meal_name", nullable = false)
    val mealName: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)