package com.gymapp.backend.domain.goal

import com.gymapp.backend.auth.JwtService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/goals")
class GoalController(
    private val goalRepository: GoalRepository,
    private val jwtService: JwtService
) {
    private fun getUserId(auth: String) = jwtService.extractUserId(auth.substring(7))

    // --- Create Goal ---
    @PostMapping
    fun createGoal(
        @RequestHeader("Authorization") auth: String,
        @RequestBody request: CreateGoalRequest
    ): ResponseEntity<GoalResponse> {
        val userId = getUserId(auth)
        val goal = goalRepository.save(
            Goal(
                userId, request.targetWeightLbs, request.startWeightLbs,
                request.targetDailyCalIntake, request.status, request.startDate
            )
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(goal.toResponse())
    }

    // --- Get Active Goal ---
    @GetMapping("/active")
    fun getActiveGoal(
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<GoalResponse> {
        val userId = getUserId(auth)
        val goal = goalRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(userId, "active")
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(goal.toResponse())
    }

    // --- Get All Goals ---
    @GetMapping
    fun getAllGoals(
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<List<GoalResponse>> {
        val userId = getUserId(auth)
        return ResponseEntity.ok(
            goalRepository.findByUserId(userId).map { it.toResponse() }
        )
    }
}

fun Goal.toResponse() = GoalResponse(
    id = this.id!!,
    targetWeightLbs = this.targetWeightLbs,
    startWeightLbs = this.startWeightLbs,
    targetDailyCalIntake = this.targetDailyCalIntake,
    status = this.status,
    startDate = this.startDate
)