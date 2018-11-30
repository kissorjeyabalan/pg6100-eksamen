package com.octopod.cinema.user.controller

import com.octopod.cinema.user.converter.UserConverter
import com.octopod.cinema.user.repository.UserRepository
import com.octopod.cinema.common.dto.UserDto
import com.octopod.cinema.common.dto.WrappedResponse
import com.octopod.cinema.common.hateos.Format
import com.octopod.cinema.common.hateos.HalLink
import com.octopod.cinema.common.hateos.HalPage
import com.octopod.cinema.user.entity.UserEntity
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@RequestMapping(
        path = ["/users"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class UserController {
    @Autowired lateinit var repo: UserRepository

    @PostMapping
    fun createUserInfo(@RequestBody userInfo: UserDto): ResponseEntity<Void> {
        if (userInfo.phone.isNullOrEmpty() || userInfo.name.isNullOrEmpty() ||userInfo.email.isNullOrEmpty()) {
            return ResponseEntity.status(400).build()
        }
        val entity = UserConverter.transform(userInfo)

        if (repo.existsById(userInfo.phone!!)) {
            return ResponseEntity.status(400).build()
        }

        val saved = repo.save(entity)
        return ResponseEntity.created(URI.create("/users/${saved.phone}")).build()
    }

    @GetMapping(path = ["/{id}"])
    fun getById(
            @PathVariable("id")
            id: String
    ): ResponseEntity<WrappedResponse<UserDto>> {
        val userInfo = repo.findById(id).orElse(null) ?:
        return ResponseEntity.status(404).body(
                WrappedResponse<UserDto>(
                        code = 404,
                        message = "Resource not found"
                ).validated()
        )

        val dto = UserConverter.transform(userInfo)

        return ResponseEntity.ok(WrappedResponse(
                code = 200,
                data = dto
        ).validated())
    }


    @GetMapping(produces = [Format.HAL_V1])
    fun getAll(
            @RequestParam("page", defaultValue = "1")
            page: Int,
            @RequestParam("limit", defaultValue = "10")
            limit: Int
    ): ResponseEntity<WrappedResponse<HalPage<UserDto>>> {
        if (page < 1 || limit < 1) {
            return ResponseEntity.status(400).body(
                    WrappedResponse<HalPage<UserDto>>(
                            code = 400,
                            message = "Malformed page or limit supplied"
                    ).validated()
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

        return ResponseEntity.ok(
                WrappedResponse(
                        code = 200,
                        data = dto
                ).validated()
        )
    }

    @PutMapping(path = ["/{id}"])
    fun replaceUser(
            @PathVariable("id")
            userId: String,
            @RequestBody
            replacement: UserDto
    ) : ResponseEntity<Void> {
        if (userId.isEmpty() || replacement.name.isNullOrEmpty() || replacement.phone.isNullOrEmpty() || replacement.email.isNullOrEmpty()) {
            return ResponseEntity.status(400).build()
        }

        if (userId != replacement.phone) {
            return ResponseEntity.status(409).build()
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
            responseCode = 400
        }

        return ResponseEntity.status(responseCode).build()
    }

    /*
    Taken from
    https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/7b9ee145f66718d5976d273b99542a374571b8cf/advanced/rest/patch/src/main/kotlin/org/tsdes/advanced/rest/patch/CounterRest.kt
    */

    @PatchMapping(path = ["/{id}"], consumes = ["application/merge-patch+json"])
    fun mergePatch(
            @PathVariable("id")
            id: String,
            @RequestBody
            jsonPatch: String
    ) : ResponseEntity<Void> {
        val entity = repo.findById(id).orElse(null)
                ?: return ResponseEntity.status(404).build()

        val jackson = ObjectMapper()

        val jsonNode: JsonNode
        try {
            jsonNode = jackson.readValue(jsonPatch, JsonNode::class.java)
        } catch (e: Exception) {
            return ResponseEntity.status(400).build()
        }

        if (jsonNode.has("phone")) {
            return ResponseEntity.status(409).build()
        }

        var newName = entity.name
        var newEmail = entity.email

        if (jsonNode.has("name")) {
            val nameNode = jsonNode.get("name")
            if (nameNode.isNull) {
                return ResponseEntity.status(400).build()
            } else if (nameNode.isTextual) {
                newName = nameNode.asText()
            }
        }

        if (jsonNode.has("email")) {
            val emailNode = jsonNode.get("email")
            if (emailNode.isNull) {
                return ResponseEntity.status(400).build()
            } else {
                newEmail = emailNode.asText()
            }
        }

        entity.name = newName
        entity.email = newEmail

        var responseCode = 204
        try {
            repo.save(entity)
        } catch (e: Exception) {
            responseCode = 400
        }

        return ResponseEntity.status(responseCode).build()
    }
}