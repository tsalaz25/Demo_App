package com.gymapp.backend.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.gymapp.backend.domain.workout.ExerciseResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

@Service
class ExerciseDbService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    @Value("\${exercisedb.api-url}")
    private lateinit var apiUrl: String

    private val webClient = WebClient.builder().build()

    fun searchExercises(name: String): List<ExerciseResult> {
        val cacheKey = "exercisedb:name:${name.lowercase().trim()}"
        return getCachedOrFetch(cacheKey) {
            val response = webClient.get()
                .uri("$apiUrl/exercises?name=${name.lowercase().trim()}&limit=15&offset=0")
                .retrieve()
                .bodyToMono(String::class.java)
                .block()!!
            parseExercises(response)
        }
    }

    fun getExercisesByBodyPart(bodyPart: String): List<ExerciseResult> {
        val cacheKey = "exercisedb:bodypart:${bodyPart.lowercase().trim()}"
        return getCachedOrFetch(cacheKey) {
            val response = webClient.get()
                .uri("$apiUrl/exercises?bodyPart=${bodyPart.lowercase().trim()}&limit=20&offset=0")
                .retrieve()
                .bodyToMono(String::class.java)
                .block()!!
            parseExercises(response)
        }
    }

    fun getExercisesByEquipment(equipment: String): List<ExerciseResult> {
        val cacheKey = "exercisedb:equipment:${equipment.lowercase().trim()}"
        return getCachedOrFetch(cacheKey) {
            val response = webClient.get()
                .uri("$apiUrl/exercises?equipment=${equipment.lowercase().trim()}&limit=20&offset=0")
                .retrieve()
                .bodyToMono(String::class.java)
                .block()!!
            parseExercises(response)
        }
    }

    private fun getCachedOrFetch(cacheKey: String, fetch: () -> List<ExerciseResult>): List<ExerciseResult> {
        val cached = redisTemplate.opsForValue().get(cacheKey)
        if (cached != null) {
            return objectMapper.readValue(
                cached,
                objectMapper.typeFactory.constructCollectionType(List::class.java, ExerciseResult::class.java)
            )
        }
        val results = fetch()
        redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(results), Duration.ofHours(24))
        return results
    }

    private fun parseExercises(json: String): List<ExerciseResult> {
        return try {
            val root = objectMapper.readTree(json)
            val dataNode = if (root.has("data")) root.path("data") else root
            if (dataNode.isArray) {
                dataNode.map { node ->
                    ExerciseResult(
                        exerciseId = node.path("exerciseId").asText(""),
                        name = node.path("name").asText(""),
                        bodyParts = node.path("bodyParts").map { it.asText() },
                        equipments = node.path("equipments").map { it.asText() },
                        targetMuscles = node.path("targetMuscles").map { it.asText() },
                        imageUrl = node.path("imageUrl").asText("").ifBlank { null }
                    )
                }
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}