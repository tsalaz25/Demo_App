package com.gymapp.backend.domain.user

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    var id: UUID? = null

    @Column(nullable = false, unique = true)
    var email: String = ""

    @Column(name = "pw_hash", nullable = false)
    var pwHash: String = ""

    @Column(name = "display_name", nullable = false)
    var displayName: String = ""

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    constructor()

    constructor(email: String, pwHash: String, displayName: String) {
        this.email = email
        this.pwHash = pwHash
        this.displayName = displayName
    }
}