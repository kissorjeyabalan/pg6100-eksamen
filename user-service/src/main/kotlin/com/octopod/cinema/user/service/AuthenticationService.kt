package com.octopod.cinema.user.service

import com.octopod.cinema.user.entity.AuthenticationEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthenticationService(
        @Autowired private val repo: AuthenticationRepository,
        @Autowired private val passwordEncoder: PasswordEncoder
) {
    fun createUser(username: String, password: String, roles: Set<String> = setOf()): Boolean {
        try {
            val hash = passwordEncoder.encode(password)
            if (repo.existsById(username)) {
                return false
            }

            val user = AuthenticationEntity(username, hash, roles.map{"ROLE_$it"}.toSet())
            repo.save(user)
            return true
        } catch (e: Exception) {
            return false
        }
    }
}

interface AuthenticationRepository : CrudRepository<AuthenticationEntity, String>