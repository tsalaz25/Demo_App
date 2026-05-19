package com.gymapp.backend.auth

import com.gymapp.backend.domain.user.User
import com.gymapp.backend.domain.user.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        if (userRepository.existsByEmail(request.email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }

        val user = User(
            email = request.email,
            pwHash = passwordEncoder.encode(request.password),
            displayName = request.displayName
        )

        val saved = userRepository.save(user)
        val token = jwtService.generateToken(saved.id!!, saved.email)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            AuthResponse(
                token = token,
                userId = saved.id!!.toString(),
                email = saved.email,
                displayName = saved.displayName
            )
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val user = userRepository.findByEmail(request.email)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        if (!passwordEncoder.matches(request.password, user.pwHash)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val token = jwtService.generateToken(user.id!!, user.email)

        return ResponseEntity.ok(
            AuthResponse(
                token = token,
                userId = user.id!!.toString(),
                email = user.email,
                displayName = user.displayName
            )
        )
    }
}