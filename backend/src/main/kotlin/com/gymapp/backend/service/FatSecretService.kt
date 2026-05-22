package com.gymapp.backend.service

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.gymapp.backend.domain.diet.FoodSearchResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

@Service
class FatSecretService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    @Value("\${fatsecret.client-id}")
    private lateinit var clientId: String

    @Value("\${fatsecret.client-secret}")
    private lateinit var clientSecret: String

    @Value("\${fatsecret.token-url}")
    private lateinit var tokenUrl: String

    @Value("\${fatsecret.api-url}")
    private lateinit var apiUrl: String

    private val webClient = WebClient.builder().build()
    private var cachedToken: String? = null
    private var tokenExpiry: Long = 0

    // --- OAuth2 Token ---
    private fun getAccessToken(): String {
        val now = System.currentTimeMillis() / 1000
        if (cachedToken != null && now < tokenExpiry - 60) {
            return cachedToken!!
        }

        val formData = LinkedMultiValueMap<String, String>()
        formData.add("grant_type", "client_credentials")
        formData.add("client_id", clientId)
        formData.add("client_secret", clientSecret)
        formData.add("scope", "basic")

        val response = webClient.post()
            .uri(tokenUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(TokenResponse::class.java)
            .block()!!

        cachedToken = response.accessToken
        tokenExpiry = now + response.expiresIn
        return cachedToken!!
    }

    // --- Food Search ---
    fun searchFoods(query: String): List<FoodSearchResult> {
        val cacheKey = "fatsecret:search:${query.lowercase().trim()}"
        val cached = redisTemplate.opsForValue().get(cacheKey)
        if (cached != null) {
            return objectMapper.readValue(
                cached,
                objectMapper.typeFactory.constructCollectionType(List::class.java, FoodSearchResult::class.java)
            )
        }

        val token = getAccessToken()
        val response = webClient.get()
            .uri { uriBuilder -> uriBuilder
                .scheme("https")
                .host("platform.fatsecret.com")
                .path("/rest/server.api")
                .queryParam("method", "foods.search")
                .queryParam("search_expression", query)
                .queryParam("format", "json")
                .queryParam("max_results", "10")
                .build()
            }
            .header("Authorization", "Bearer $token")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()!!

            // API Debug
            //println("FATSECRET RAW RESPONSE: $response")  // ADD THIS LINE

        val results = parseFoodSearchResponse(response)
        redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(results), Duration.ofHours(24))
        return results
    }

    // --- Parse FatSecret Response ---
    private fun parseFoodSearchResponse(json: String): List<FoodSearchResult> {
        return try {
            val root = objectMapper.readTree(json)
            val foodsNode = root.path("foods").path("food")
            if (foodsNode.isMissingNode || foodsNode.isNull) return emptyList()

            val foods = if (foodsNode.isArray) foodsNode.toList() else listOf(foodsNode)

            foods.mapNotNull { food ->
                try {
                    val desc = food.path("food_description").asText("")
                    val nutrients = parseDescription(desc)
                    FoodSearchResult(
                        foodId = food.path("food_id").asText(""),
                        foodName = food.path("food_name").asText(""),
                        brandName = food.path("brand_name").asText("").ifBlank { null },
                        calories = nutrients["calories"] ?: 0f,
                        protein = nutrients["protein"] ?: 0f,
                        carbs = nutrients["carbs"] ?: 0f,
                        fat = nutrients["fat"] ?: 0f,
                        servingDescription = desc.substringBefore(" - ").trim()
                    )
                } catch (e: Exception) { null }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Parses "Per 100g - Calories: 250kcal | Fat: 5.00g | Carbs: 30.00g | Protein: 10.00g"
    private fun parseDescription(desc: String): Map<String, Float> {
        val result = mutableMapOf<String, Float>()
        try {
            val part = desc.substringAfter(" - ")
            part.split("|").forEach { segment ->
                val clean = segment.trim()
                when {
                    clean.startsWith("Calories:") ->
                        result["calories"] = clean.removePrefix("Calories:").trim().removeSuffix("kcal").trim().toFloatOrNull() ?: 0f
                    clean.startsWith("Fat:") ->
                        result["fat"] = clean.removePrefix("Fat:").trim().removeSuffix("g").trim().toFloatOrNull() ?: 0f
                    clean.startsWith("Carbs:") ->
                        result["carbs"] = clean.removePrefix("Carbs:").trim().removeSuffix("g").trim().toFloatOrNull() ?: 0f
                    clean.startsWith("Protein:") ->
                        result["protein"] = clean.removePrefix("Protein:").trim().removeSuffix("g").trim().toFloatOrNull() ?: 0f
                }
            }
        } catch (e: Exception) { }
        return result
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class TokenResponse(
        @JsonProperty("access_token") val accessToken: String,
        @JsonProperty("expires_in") val expiresIn: Long
    )
}