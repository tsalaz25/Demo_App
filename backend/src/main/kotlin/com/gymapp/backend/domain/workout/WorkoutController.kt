package com.gymapp.backend.domain.workout

import com.gymapp.backend.auth.JwtService
import com.gymapp.backend.service.ExerciseDbService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/workout")
class WorkoutController(
    private val splitRepository: WorkoutSplitRepository,
    private val splitDayRepository: SplitDayRepository,
    private val splitDayWorkoutRepository: SplitDayWorkoutRepository,
    private val workoutLogRepository: WorkoutLogRepository,
    private val workoutPerformedRepository: WorkoutPerformedRepository,
    private val exerciseDbService: ExerciseDbService,
    private val jwtService: JwtService
) {
    private fun getUserId(authHeader: String) =
        jwtService.extractUserId(authHeader.substring(7))

    // --- Exercise Search ---
    @GetMapping("/exercises/search")
    fun searchExercises(
        @RequestHeader("Authorization") auth: String,
        @RequestParam name: String
    ) = ResponseEntity.ok(exerciseDbService.searchExercises(name))

    @GetMapping("/exercises/bodypart/{bodyPart}")
    fun getByBodyPart(
        @RequestHeader("Authorization") auth: String,
        @PathVariable bodyPart: String
    ) = ResponseEntity.ok(exerciseDbService.getExercisesByBodyPart(bodyPart))

    @GetMapping("/exercises/equipment/{equipment}")
    fun getByEquipment(
        @RequestHeader("Authorization") auth: String,
        @PathVariable equipment: String
    ) = ResponseEntity.ok(exerciseDbService.getExercisesByEquipment(equipment))

    // --- Splits ---
    @PostMapping("/splits")
    fun createSplit(
        @RequestHeader("Authorization") auth: String,
        @RequestBody request: CreateSplitRequest
    ): ResponseEntity<SplitResponse> {
        val userId = getUserId(auth)
        val split = splitRepository.save(
            WorkoutSplit(userId, request.splitName, request.description)
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(split.toResponse())
    }

    @GetMapping("/splits")
    fun getSplits(
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<List<SplitResponse>> {
        val userId = getUserId(auth)
        val splits = splitRepository.findByUserId(userId)
        val responses = splits.map { split ->
            val days = splitDayRepository.findBySplitId(split.id!!)
            split.toResponse(days.map { day ->
                val exercises = splitDayWorkoutRepository.findBySplitDayId(day.id!!)
                day.toResponse(exercises.map { it.toResponse() })
            })
        }
        return ResponseEntity.ok(responses)
    }

    // --- Split Days ---
    @PostMapping("/split-days")
    fun createSplitDay(
        @RequestHeader("Authorization") auth: String,
        @RequestBody request: CreateSplitDayRequest
    ): ResponseEntity<SplitDayResponse> {
        val day = splitDayRepository.save(
            SplitDay(request.splitId, request.onDay, request.workoutName)
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(day.toResponse())
    }

    // --- Add Exercise to Day ---
    @PostMapping("/split-days/exercises")
    fun addExercise(
        @RequestHeader("Authorization") auth: String,
        @RequestBody request: AddExerciseRequest
    ): ResponseEntity<SplitDayWorkoutResponse> {
        val exercise = splitDayWorkoutRepository.save(
            SplitDayWorkout(
                request.splitDayId, request.workoutIdApi,
                request.exerciseName, request.muscleGroup, request.dispOrder
            )
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(exercise.toResponse())
    }

    // --- Workout Logs ---
    @PostMapping("/logs")
    fun createWorkoutLog(
        @RequestHeader("Authorization") auth: String,
        @RequestBody request: CreateWorkoutLogRequest
    ): ResponseEntity<WorkoutLogResponse> {
        val userId = getUserId(auth)
        val log = workoutLogRepository.save(
            WorkoutLog(userId, request.splitDayId, request.performedOn, request.durationMin)
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(log.toResponse())
    }

    @GetMapping("/logs")
    fun getWorkoutLogs(
        @RequestHeader("Authorization") auth: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<List<WorkoutLogResponse>> {
        val userId = getUserId(auth)
        val logs = workoutLogRepository.findByUserIdAndPerformedOn(userId, date)
        val responses = logs.map { log ->
            val sets = workoutPerformedRepository.findByWorkoutLogId(log.id!!)
            log.toResponse(sets)
        }
        return ResponseEntity.ok(responses)
    }

    // --- Log a Set ---
    @PostMapping("/logs/sets")
    fun logSet(
        @RequestHeader("Authorization") auth: String,
        @RequestBody request: LogSetRequest
    ): ResponseEntity<WorkoutPerformedResponse> {
        val set = workoutPerformedRepository.save(
            WorkoutPerformed(
                request.workoutLogId, request.workoutIdApi, request.setNumber,
                request.reps, request.weightForSetLbs, request.completed
            )
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(set.toResponse())
    }

    // --- History for a split day ---
    @GetMapping("/logs/history/{splitDayId}")
    fun getHistory(
        @RequestHeader("Authorization") auth: String,
        @PathVariable splitDayId: java.util.UUID
    ): ResponseEntity<List<WorkoutLogResponse>> {
        val userId = getUserId(auth)
        val logs = workoutLogRepository.findByUserIdAndSplitDayId(userId, splitDayId)
        val responses = logs.map { log ->
            val sets = workoutPerformedRepository.findByWorkoutLogId(log.id!!)
            log.toResponse(sets)
        }
        return ResponseEntity.ok(responses)
    }
}

// --- Extension functions ---
fun WorkoutSplit.toResponse(days: List<SplitDayResponse> = emptyList()) = SplitResponse(
    id = this.id!!,
    splitName = this.splitName,
    description = this.description,
    isActive = this.isActive,
    days = days
)

fun SplitDay.toResponse(exercises: List<SplitDayWorkoutResponse> = emptyList()) = SplitDayResponse(
    id = this.id!!,
    onDay = this.onDay,
    workoutName = this.workoutName,
    exercises = exercises
)

fun SplitDayWorkout.toResponse() = SplitDayWorkoutResponse(
    id = this.id!!,
    workoutIdApi = this.workoutIdApi,
    exerciseName = this.exerciseName,
    muscleGroup = this.muscleGroup,
    dispOrder = this.dispOrder
)

fun WorkoutLog.toResponse(sets: List<WorkoutPerformed> = emptyList()) = WorkoutLogResponse(
    id = this.id!!,
    splitDayId = this.splitDayId,
    performedOn = this.performedOn,
    durationMin = this.durationMin,
    sets = sets.map { it.toResponse() }
)

fun WorkoutPerformed.toResponse() = WorkoutPerformedResponse(
    id = this.id!!,
    workoutIdApi = this.workoutIdApi,
    setNumber = this.setNumber,
    reps = this.reps,
    weightForSetLbs = this.weightForSetLbs,
    completed = this.completed
)