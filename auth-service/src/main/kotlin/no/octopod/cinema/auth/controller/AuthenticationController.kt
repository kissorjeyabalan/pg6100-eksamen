package no.octopod.cinema.auth.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import no.octopod.cinema.auth.dto.LoginDto
import no.octopod.cinema.auth.service.AuthenticationService
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.*
import java.lang.Exception

@CrossOrigin
@Api(value = "/auth", description = "Authentication Requests")
@RequestMapping(
        path =["/auth"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class AuthenticationController(
        private val authService: AuthenticationService,
        private val authenticationManager: AuthenticationManager,
        private val userDetailsService: UserDetailsService
) {

    @ApiOperation("Log in and set session")
    @PostMapping(path = ["/login"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ApiResponse(code = 204, message = "Successfully logged in")
    fun login(@RequestBody creds: LoginDto): ResponseEntity<Void> {
        if ((creds.username.isNullOrEmpty() || creds.password.isNullOrEmpty())) {
            return ResponseEntity.status(400).build()
        }

        return try {
            val userDetails = userDetailsService.loadUserByUsername(creds.username!!)
            val token = UsernamePasswordAuthenticationToken(userDetails, creds.password, userDetails.authorities)
            authenticationManager.authenticate(token)
            if (token.isAuthenticated) {
                SecurityContextHolder.getContext().authentication = token
            }
            ResponseEntity.status(204).build()
        } catch (e: Exception) {
            ResponseEntity.status(401).build()
        }
    }

    @ApiOperation("Create a new user")
    @PostMapping(path = ["/register"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ApiResponse(code = 201, message = "Successfully created new user")
    fun register(@RequestBody creds: LoginDto): ResponseEntity<Void> {
        val registered = authService.createUser(creds.username!!, creds.password!!, setOf("USER"))
        if (!registered) {
            return ResponseEntity.status(400).build()
        }

        return ResponseEntity.status(204).build()
    }
}