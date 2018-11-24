package com.octopod.cinema.kino.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Throwables
import com.octopod.cinema.kino.converter.TheaterConverter
import com.octopod.cinema.kino.dto.TheaterDto
import com.octopod.cinema.kino.repository.TheaterRepository
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.http.MediaType
import com.octopod.cinema.common.dto.WrappedResponse
import com.octopod.cinema.kino.entity.Theater
import io.swagger.annotations.ApiParam
import com.octopod.cinema.common.hateos.Format
import com.octopod.cinema.common.hateos.HalLink
import com.octopod.cinema.common.hateos.HalPage
import javax.validation.ConstraintViolationException

@Api(value = "theaters", description = "Handling of theaters")
@RequestMapping(
        path = ["/theaters"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)

@RestController
class TheaterController {

    @Autowired
    lateinit var repo: TheaterRepository

    @ApiOperation("Create a new theater")
    @PostMapping
    fun createTheater(

            @ApiParam("Show dto")
            @RequestBody dto: TheaterDto

    ): ResponseEntity<Void> {

        if (dto.name == null || dto.seatsMax == null) {
            return ResponseEntity.status(400).build()
        }

        val created = repo.save(Theater(dto.name!!, dto.seatsMax!!))

        return ResponseEntity.created(
                UriComponentsBuilder
                        .fromPath("/theaters/${created.id!!}")
                        .build()
                        .toUri()
        ).build()
    }

    @ApiOperation("Get all theaters")
    @GetMapping(produces = [Format.HAL_V1])
    fun getAllTheaters(

        @ApiParam("Page number")
        @RequestParam("page", defaultValue = "1")
        page: String,

        @ApiParam("Limit")
        @RequestParam("limit", defaultValue = "10")
        limit: String

    ): ResponseEntity<WrappedResponse<HalPage<TheaterDto>>> {

        val pageInt = page.toInt()
        val limitInt = limit.toInt()

        if (pageInt < 1 || limitInt < 1) {
            return ResponseEntity.status(400).body(
                    WrappedResponse<HalPage<TheaterDto>>(
                            code = 400,
                            message = "Malformed limit og page number supplied"
                    ).validated()
            )
        }


        val entryList = repo.findAll().toList()
        val dto = TheaterConverter.transform(entryList, pageInt, limitInt)

        val uriBuilder = UriComponentsBuilder.fromPath("/theaters")
        dto._self = HalLink(uriBuilder.cloneBuilder()
                .queryParam("page", page)
                .queryParam("limit", limit)
                .build().toString())

        if (!entryList.isEmpty() && pageInt > 1) {
            dto.previous = HalLink(uriBuilder.cloneBuilder()
                    .queryParam("page", (pageInt - 1).toString())
                    .queryParam("limit", limit)
                    .build().toString())
        }

        if (((pageInt) * limitInt) < entryList.size) {
            dto.next = HalLink(uriBuilder.cloneBuilder()
                    .queryParam("page", (pageInt + 1).toString())
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

    @ApiOperation("Get ticket with specific id")
    @GetMapping(path = ["/{id}"])
    fun getTheater(

            @ApiParam("Theater id")
            @PathVariable("id")
            id: String

    ): ResponseEntity<WrappedResponse<TheaterDto>> {

        val pathId: Long
        try {
            pathId = id.toLong()
        } catch (e: Exception) {
            /*
                invalid id. But here we return 404 instead of 400,
                as in the API we defined the id as string instead of long
             */
            return ResponseEntity.status(404).build()
        }

        val entity = repo.findById(pathId).orElse(null) ?: return ResponseEntity.status(404).build()
        val dto = TheaterConverter.transform(entity)

        return ResponseEntity.ok(WrappedResponse(
                code = 200,
                data = dto
        ).validated())
    }

    @ApiOperation("Delete theater with specific id")
    @DeleteMapping(path = ["/{id}"])
    fun deleteTheaterById(

            @ApiParam("Theater id")
            @PathVariable("id")
            id: String

    ): ResponseEntity<WrappedResponse<TheaterDto>> {

        val pathId: Long
        try {
            pathId = id.toLong()
        } catch (e: Exception) {
            /*
                invalid id. But here we return 404 instead of 400,
                as in the API we defined the id as string instead of long
             */
            return ResponseEntity.status(404).build()
        }

        repo.deleteById(pathId)

        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Update a theater")
    @PutMapping(path = ["/{id}"])
    fun updateTheater(

            @ApiParam("Theater id")
            @PathVariable("id")
            id: String,

            @ApiParam("Updated theater dto")
            @RequestBody
            dto: TheaterDto

    ): ResponseEntity<WrappedResponse<TheaterDto>> {

        val pathId: Long
        val dtoId: Long
        try {
            pathId = id.toLong()
            dtoId = dto.id!!.toLong()
        } catch (e: Exception) {
            /*
                invalid id. But here we return 404 instead of 400,
                as in the API we defined the id as string instead of long
             */
            return ResponseEntity.status(404).build()
        }

        if (dtoId != pathId) {
            return ResponseEntity.status(409).build()
        }

        if (!repo.existsById(dtoId)) {
            return ResponseEntity.status(404).build()
        }

        if (dto.name == null || dto.seatsMax == null) {
            return ResponseEntity.status(400).build()
        }

        val theater = TheaterConverter.transform(dto)

        try {
            repo.save(theater)
        } catch (e: Exception) {
            if(Throwables.getRootCause(e) is ConstraintViolationException) {
                return ResponseEntity.status(400).build()
            }
            throw e
        }

        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Update a field in a theater")
    @PatchMapping(path = ["/{id}"])
    fun patchTheater(

            @ApiParam("Theater id")
            @PathVariable("id")
            id: String,

            @ApiParam("New theater JSON")
            @RequestBody
            json: String

    ) : ResponseEntity<WrappedResponse<TheaterDto>> {

        val pathId: Long
        try {
            pathId = id.toLong()
        } catch (e: Exception) {
            /*
                invalid id. But here we return 404 instead of 400,
                as in the API we defined the id as string instead of long
             */
            return ResponseEntity.status(404).build()
        }

        val originalDto = repo.findById(pathId).orElse(null) ?: return ResponseEntity.status(404).body(
                WrappedResponse<TheaterDto>(
                        code = 404,
                        message = "Resource not found"
                ).validated()
        )

        //taken from arcuri82 CounterRestPatch
        val mapper = ObjectMapper()

        val jsonNode: JsonNode
        try {
            jsonNode = mapper.readValue(json, JsonNode::class.java)
        } catch (e: Exception) {
            //Invalid JSON data as input
            return ResponseEntity.status(400).build()
        }

        if (jsonNode.has("id")) {
            //shouldn't be allowed to modify the counter id
            return ResponseEntity.status(409).build()
        }

        var newName = originalDto.name
        var newSeatsMax = originalDto.seatsMax

        if (jsonNode.has("name")) {
            val nameNode = jsonNode.get("name")
            newName = when {
                nameNode.isNull -> return ResponseEntity.status(400).build()
                nameNode.isTextual -> nameNode.asText()
                else -> //Invalid JSON. Non-string name
                    return ResponseEntity.status(400).build()
            }
        }

        if (jsonNode.has("seatsMax")) {
            val maxNode = jsonNode.get("seatsMax")
            newSeatsMax = when {
                maxNode.isNull -> return ResponseEntity.status(400).build()
                maxNode.isNumber -> maxNode.asInt()
                else -> //Invalid JSON. Non-string name
                    return ResponseEntity.status(400).build()
            }
        }

        originalDto.name = newName
        originalDto.seatsMax = newSeatsMax

        repo.save(originalDto)
        return ResponseEntity.status(204).build()
    }
}