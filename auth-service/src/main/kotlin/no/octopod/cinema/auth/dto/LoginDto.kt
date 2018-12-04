package no.octopod.cinema.auth.dto

data class LoginDto(
        var username: String? = null,
        var password: String? = null
)