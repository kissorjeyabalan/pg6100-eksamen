package no.octopod.cinema.auth.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import no.octopod.cinema.auth.dto.AuthDto
import no.octopod.cinema.auth.service.AuthenticationService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import java.security.Principal

@Api(value = "auth", description = "Authentication Requests")
@RestController
class AuthenticationController(
        private val authService: AuthenticationService,
        private val authenticationManager: AuthenticationManager,
        private val userDetailsService: UserDetailsService
) {

    @ApiOperation("Log in and set session")
    @PostMapping(path = ["/login"], consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @ApiResponse(code = 204, message = "Successfully logged in")
    fun login(@RequestBody creds: AuthDto): ResponseEntity<Void> {
        if ((creds.username.isNullOrEmpty() || creds.password.isNullOrEmpty())) {
            return ResponseEntity.status(400).build()
        }

        val userDetails = try{
            userDetailsService.loadUserByUsername(creds.username!!)
        } catch (e: UsernameNotFoundException){
            return ResponseEntity.status(400).build()
        }

        val token = UsernamePasswordAuthenticationToken(userDetails, creds.password!!, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
            return ResponseEntity.status(204).build()
        }

        return ResponseEntity.status(400).build()
    }

    @ApiOperation("Create a new user")
    @PostMapping(path = ["/register"], consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @ApiResponse(code = 201, message = "Successfully created new user")
    fun register(@RequestBody creds: AuthDto): ResponseEntity<Void> {
        if ((creds.username.isNullOrEmpty() || creds.password.isNullOrEmpty())) {
            return ResponseEntity.status(400).build()
        }

        val registered = authService.createUser(creds.username!!, creds.password!!, setOf("USER"))
        if (!registered) {
            return ResponseEntity.status(400).build()
        }

        val userDetails = userDetailsService.loadUserByUsername(creds.username)
        val token = UsernamePasswordAuthenticationToken(userDetails, creds.password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
        }

        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Get the currently logged in user roles")
    @RequestMapping(path = ["/user"])
    fun getCurrentUser(user: Principal): ResponseEntity<Map<String, Any>> {
        val map = mutableMapOf<String, Any>()
        map["name"] = user.name
        map["roles"] = AuthorityUtils.authorityListToSet((user as Authentication).authorities)
        return ResponseEntity.ok(map)
    }
}