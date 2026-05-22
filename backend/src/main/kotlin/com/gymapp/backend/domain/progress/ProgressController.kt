package com.gymapp.backend.domain.progress

import com.gymapp.backend.auth.JwtService
import com.gymapp.backend.service.WeeklyAggregatorService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/progress")
class ProgressController(
    private val weightLogRepository: WeightLogRepository,
    private val weeklySummaryRepository: WeeklySummaryRepository,
    private val weeklyAggregatorService: WeeklyAggregatorService,
    private val jwtService: JwtService
) {
    private fun getUserId(auth: String) = jwtService.extractUserId(auth.substring(7))

    // --- Log Weight ---
    @PostMapping("/weight")
    fun logWeight(
        @RequestHeader("Authorization") auth: String,
        @RequestBody request: LogWeightRequest
    ): ResponseEntity<WeightLogResponse> {
        val userId = getUserId(auth)
        val log = weightLogRepository.save(
            WeightLog(userId, request.weightLbs, request.onDate, request.notes)
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(log.toResponse())
    }

    // --- Get Weight History ---
    @GetMapping("/weight")
    fun getWeightHistory(
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<List<WeightLogResponse>> {
        val userId = getUserId(auth)
        return ResponseEntity.ok(
            weightLogRepository.findByUserIdOrderByOnDateDesc(userId).map { it.toResponse() }
        )
    }

    // --- Get Latest Weight ---
    @GetMapping("/weight/latest")
    fun getLatestWeight(
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<WeightLogResponse> {
        val userId = getUserId(auth)
        val latest = weightLogRepository.findFirstByUserIdOrderByOnDateDesc(userId)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(latest.toResponse())
    }

    // --- Get Weekly Summaries ---
    @GetMapping("/weekly")
    fun getWeeklySummaries(
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<List<WeeklySummaryResponse>> {
        val userId = getUserId(auth)
        return ResponseEntity.ok(
            weeklySummaryRepository.findByUserIdOrderByWeekStartDesc(userId).map { it.toResponse() }
        )
    }

    // --- Manually trigger aggregator (for testing) ---
    @PostMapping("/weekly/aggregate")
    fun triggerAggregation(
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<String> {
        weeklyAggregatorService.aggregateLastWeek()
        return ResponseEntity.ok("Aggregation complete")
    }
}

fun WeightLog.toResponse() = WeightLogResponse(
    id = this.id!!,
    weightLbs = this.weightLbs,
    onDate = this.onDate,
    notes = this.notes
)

fun WeeklySummary.toResponse() = WeeklySummaryResponse(
    id = this.id!!,
    weekStart = this.weekStart,
    weekEnd = this.weekEnd,
    avgCals = this.avgCals,
    workoutsCompleted = this.workoutsCompleted,
    weightDiff = this.weightDiff
)