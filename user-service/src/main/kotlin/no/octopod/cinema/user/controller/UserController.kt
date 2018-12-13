package no.octopod.cinema.user.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.octopod.cinema.user.converter.UserConverter
import no.octopod.cinema.user.repository.UserRepository
import no.octopod.cinema.common.dto.UserInfoDto
import no.octopod.cinema.common.utility.ResponseUtil.getWrappedResponse
import no.octopod.cinema.common.dto.WrappedResponse
import no.octopod.cinema.common.hateos.Format
import no.octopod.cinema.common.hateos.HalLink
import no.octopod.cinema.common.hateos.HalPage
import no.octopod.cinema.common.utility.SecurityUtil.isAuthenticatedOrAdmin
import no.octopod.cinema.user.entity.UserEntity
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@RequestMapping(
        path = ["/users"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
@Api(value = "users", description = "Api for persisting and retrieving information about users")
class UserController {
    @Autowired lateinit var repo: UserRepository

    @PostMapping
    @ApiOperation(value = "Create a new user info", notes = "Will only create UserInfo for the logged in user, and only once")
    fun createUserInfo(
            @ApiParam(name = "UserInfo Object", value = "Data to persist about the user. ID must not be sent, and will be ignored.")
            @RequestBody userInfo: UserInfoDto,
            authentication: Authentication
            ): ResponseEntity<WrappedResponse<Void>> {
        if (userInfo.phone.isNullOrEmpty() || userInfo.name.isNullOrEmpty() ||userInfo.email.isNullOrEmpty()) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Phone, name and email can not be null or empty"
            )
        }

        if (!isAuthenticatedOrAdmin(authentication, userInfo.phone!!)) {
            return getWrappedResponse(
                    rawStatusCode = 403,
                    message = "User does not have access to this resource"
            )
        }

        val entity = UserConverter.transform(userInfo)

        if (repo.existsById(userInfo.phone!!)) {
            return getWrappedResponse(
                    rawStatusCode = 409,
                    message = "Phone already in use"
            )
        }

        val saved = repo.save(entity)
        return ResponseEntity.created(URI.create("/users/${saved.phone}")).body(WrappedResponse(
                code = 201,
                message = "UserInfo created"
        ))
    }

    @GetMapping(path = ["/{userId}"])
    @ApiOperation(value = "Retrieve user information for specific user")
    fun getById(
            @PathVariable("userId")
            @ApiParam("User ID")
            userId: String,
            authentication: Authentication
    ): ResponseEntity<WrappedResponse<UserInfoDto>> {
        if (!isAuthenticatedOrAdmin(authentication, userId)) {
            return getWrappedResponse(
                    rawStatusCode = 403,
                    message = "Forbidden"
            )
        }

        val userInfo = repo.findById(userId).orElse(null) ?: return getWrappedResponse(
                rawStatusCode = 404,
                message = "UserInfo does not exist"
        )

        val dto = UserConverter.transform(userInfo)

        return getWrappedResponse(
                rawStatusCode = 200,
                data = dto
        )
    }


    @GetMapping(produces = [Format.HAL_V1])
    @ApiOperation("Used to retrieve all user information", notes = "This endpoint is only accessible with an administrator account")
    fun getAll(
            @RequestParam("page", defaultValue = "1")
            page: Int,
            @RequestParam("limit", defaultValue = "10")
            limit: Int,
            authentication: Authentication
    ): ResponseEntity<WrappedResponse<HalPage<UserInfoDto>>> {

        if (!isAuthenticatedOrAdmin(authentication)) {
            return getWrappedResponse(
                    rawStatusCode = 403,
                    message = "Forbidden"
            )
        }

        if (page < 1 || limit < 1) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Invalid page or limit supplied"
            )
        }
        val entityList = repo.findAll().toList()
        val dto = UserConverter.transform(entityList, page, limit)

        val uriBuilder = UriComponentsBuilder.fromPath("/users")
        dto._self = HalLink(uriBuilder.cloneBuilder()
                .queryParam("page", page)
                .queryParam("limit", limit)
                .build().toString())

        if (!entityList.isEmpty() && page > 1) {
            dto.previous = HalLink(uriBuilder.cloneBuilder()
                    .queryParam("page", page - 1)
                    .queryParam("limit", limit)
                    .build().toString())
        }

        if (((page) * limit) < entityList.size) {
            dto.next = HalLink(uriBuilder.cloneBuilder()
                    .queryParam("page", page + 1)
                    .queryParam("limit", limit)
                    .build().toString())
        }

        return getWrappedResponse(
            rawStatusCode = 200,
            data = dto
        )
    }

    @PutMapping(path = ["/{userId}"])
    @ApiOperation("Replace everything about a user.")
    fun replaceUser(
            @PathVariable("userId")
            @ApiParam("User ID to replace information for")
            userId: String,
            @RequestBody
            @ApiParam("The new information replace existing information with. ALL FIELDS are -REQUIRED- except created and updated")
            replacement: UserInfoDto,
            authentication: Authentication
    ) : ResponseEntity<WrappedResponse<Void>> {
        if (!isAuthenticatedOrAdmin(authentication, userId)) {
            return getWrappedResponse(
                    rawStatusCode = 403,
                    message = "Forbidden"
            )
        }

        if (replacement.name.isNullOrEmpty() || replacement.phone.isNullOrEmpty() || replacement.email.isNullOrEmpty()) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Name, phone and email can not be null or empty"
            )
        }

        if (userId != replacement.phone) {
            return getWrappedResponse(
                    rawStatusCode = 409,
                    message = "Can not replace phone"
            )
        }

        var entity = repo.findById(userId).orElse(null)
        var responseCode = 204
        if (entity != null) {
            entity.name = replacement.name
            entity.email = replacement.email
        } else {
            entity = UserEntity(phone = replacement.phone, name = replacement.name, email = replacement.email)
            responseCode = 201
        }


        try {
            repo.save(entity)
        } catch (e: Exception) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Constraints failure"
            )
        }

        if (responseCode == 201) {
            return ResponseEntity.created(URI.create("/users/${entity.phone}")).body(WrappedResponse(
                    code = 201,
                    message = "UserInfo created"
            ))
        }
        return ResponseEntity.status(204).build()
    }

    /*
    Taken from
    https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/7b9ee145f66718d5976d273b99542a374571b8cf/advanced/rest/patch/src/main/kotlin/org/tsdes/advanced/rest/patch/CounterRest.kt
    */

    @PatchMapping(path = ["/{userId}"], consumes = ["application/merge-patch+json"])
    @ApiOperation("Merge patch to swap certain information about a user")
    fun mergePatch(
            @PathVariable("userId")
            @ApiParam("User ID to patch")
            userId: String,
            @RequestBody
            @ApiParam("JSON Object with items to patch. Accepted nodes: name (string), email (string)")
            jsonPatch: String,
            authentication: Authentication
    ) : ResponseEntity<WrappedResponse<Void>> {
        if (!isAuthenticatedOrAdmin(authentication, userId)) {
            return getWrappedResponse(
                    rawStatusCode = 403,
                    message = "Forbidden"
            )
        }

        val entity = repo.findById(userId).orElse(null)
                ?: return getWrappedResponse(
                        rawStatusCode = 404,
                        message = "UserInfo not found"
                )

        val jackson = ObjectMapper()

        val jsonNode: JsonNode
        try {
            jsonNode = jackson.readValue(jsonPatch, JsonNode::class.java)
        } catch (e: Exception) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Malformed JSON supplied"
            )
        }

        if (jsonNode.has("phone")) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Merge patch does not support changing phone"
            )
        }

        var newName = entity.name
        var newEmail = entity.email

        if (jsonNode.has("name")) {
            val nameNode = jsonNode.get("name")
            when {
                nameNode.isNull -> return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "Name can not be null"
                )
                nameNode.isTextual -> newName = nameNode.asText()
                else -> return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "Name must be of type string"
                )
            }
        }

        if (jsonNode.has("email")) {
            val emailNode = jsonNode.get("email")
            when {
                emailNode.isNull -> return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "Email can not be null"
                )
                emailNode.isTextual -> newEmail = emailNode.asText()
                else -> return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "Email must be of type string"
                )
            }
        }

        entity.name = newName
        entity.email = newEmail

        try {
            repo.save(entity)
        } catch (e: Exception) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Constraints failure"
            )
        }

        return ResponseEntity.status(204).build()
    }

    @DeleteMapping(path = ["/{userId}"])
    @ApiOperation("Delete existing user information")
    fun deleteUser(
            @PathVariable
            @ApiParam("User ID to delete")
            userId: String
    ): ResponseEntity<WrappedResponse<Void>> {
        val userInfoExists = repo.existsById(userId)
        return if (userInfoExists) {
            repo.deleteById(userId)
            ResponseEntity.status(204).build()
        } else {
            return getWrappedResponse(
                    rawStatusCode = 404,
                    message = "UserInfo not found"
            )
        }
    }
}