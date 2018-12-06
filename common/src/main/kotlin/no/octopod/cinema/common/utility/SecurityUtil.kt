package no.octopod.cinema.common.utility

import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority

object SecurityUtil {
    fun isAuthenticatedOrAdmin(authentication: Authentication, id: String): Boolean {
        print(authentication)
        if (!authentication.isAuthenticated) return false
        if (authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN"))) return true
        if (authentication.name == id) return true
        return false
    }
}