package com.octopod.cinema.user.controller

import com.octopod.cinema.user.converter.UserConverter
import com.octopod.cinema.user.repository.UserRepository
import com.octopod.cinema.common.dto.UserDto
import com.octopod.cinema.common.dto.WrappedResponse
import com.octopod.cinema.common.hateos.Format
import com.octopod.cinema.common.hateos.HalLink
import com.octopod.cinema.common.hateos.HalPage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@RequestMapping(
        path = ["/users"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
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
        return ResponseEntity.created(URI.create("/users/${saved.name}")).build()
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
    ) : ResponseEntity<WrappedResponse<Void>> {
        if (userId.isEmpty() || replacement.name.isNullOrEmpty() || replacement.name != userId) {
            return ResponseEntity.status(400).build()
        }

        if (!repo.existsById(userId)) {
            return ResponseEntity.status(404).build();
        }

        if (replacement.email.isNullOrEmpty() || replacement.phone.isNullOrEmpty()) {
            return ResponseEntity.status(400).build()
        }

        val user = UserConverter.transform(replacement)
        repo.save(user)

        return ResponseEntity.status(204).build()
    }
}