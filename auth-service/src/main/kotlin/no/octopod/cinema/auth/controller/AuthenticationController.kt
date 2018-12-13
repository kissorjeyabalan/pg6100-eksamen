package no.octopod.cinema.auth.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.octopod.cinema.common.utility.ResponseUtil.getWrappedResponse
import no.octopod.cinema.auth.dto.AuthDto
import no.octopod.cinema.auth.service.AuthenticationService
import no.octopod.cinema.common.dto.WrappedResponse
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

    @ApiOperation("Log in and get a session cookie back.")
    @PostMapping(path = ["/login"], consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun login(
            @ApiParam()
            @RequestBody
            creds: AuthDto
    ): ResponseEntity<WrappedResponse<Void>> {
        if ((creds.username.isNullOrEmpty() || creds.password.isNullOrEmpty())) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Invalid Request Body: Username or Password is empty."
            )
        }

        val userDetails = try{
            userDetailsService.loadUserByUsername(creds.username!!)
        } catch (e: UsernameNotFoundException){
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Invalid username/password"
            )
        }

        val token = UsernamePasswordAuthenticationToken(userDetails, creds.password!!, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
            return ResponseEntity.status(204).build()
        }

        return getWrappedResponse(
                rawStatusCode = 400,
                message = "Invalid username/password"
        )
    }

    @ApiOperation("Create a new user")
    @PostMapping(path = ["/register"], consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun register(@RequestBody creds: AuthDto): ResponseEntity<WrappedResponse<Void>> {
        if ((creds.username.isNullOrEmpty() || creds.password.isNullOrEmpty())) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Invalid Request Body: Username or password not supplied"
            )
        }

        val registered = authService.createUser(creds.username!!, creds.password!!, setOf("USER"))
        if (!registered) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Username already in use"
            )
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
    fun getCurrentUser(user: Principal): ResponseEntity<WrappedResponse<Map<String, Any>>> {
        val map = mutableMapOf<String, Any>()
        map["name"] = user.name
        map["roles"] = AuthorityUtils.authorityListToSet((user as Authentication).authorities)
        return getWrappedResponse(
                rawStatusCode = 200,
                data = map
        )
    }
}