package com.gymapp.backend.domain.diet

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "meals")
class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    var id: UUID? = null

    @Column(name = "u_id", nullable = false)
    var userId: UUID = UUID.randomUUID()

    @Column(name = "meal_date", nullable = false)
    var mealDate: LocalDate = LocalDate.now()

    @Column(name = "meal_name", nullable = false)
    var mealName: String = ""

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    constructor()

    constructor(userId: UUID, mealDate: LocalDate, mealName: String) {
        this.userId = userId
        this.mealDate = mealDate
        this.mealName = mealName
    }
}