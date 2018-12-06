package no.octopod.cinema.common.utility

import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority

object SecurityUtil {
    fun isAuthenticatedOrAdmin(authentication: Authentication, providedId: String?): Boolean {
        if (!authentication.isAuthenticated) return false
        if (authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN"))) return true
        if (!providedId.isNullOrBlank()) {
            if (authentication.name == providedId) return true
        }
        return false
    }

}