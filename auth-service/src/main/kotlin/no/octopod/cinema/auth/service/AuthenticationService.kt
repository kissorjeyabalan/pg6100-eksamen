package no.octopod.cinema.auth.service

import no.octopod.cinema.auth.entity.AuthenticationEntity
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

            val user = AuthenticationEntity(username, hash, roles.map { "ROLE_$it" }.toSet())
            repo.save(user)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun initializeDefaultUsers() {
        try {
            val hash = passwordEncoder.encode("admin")
            val roles: Set<String> = setOf("ADMIN", "USER")

            val user = AuthenticationEntity("admin", hash, roles.map { "ROLE_$it" }.toSet())
            repo.save(user)
        } catch (e: Exception) {
            // do nothing
        }
    }
}

interface AuthenticationRepository : CrudRepository<AuthenticationEntity, String>