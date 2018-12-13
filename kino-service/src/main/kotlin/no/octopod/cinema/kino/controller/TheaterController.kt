package no.octopod.cinema.kino.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Throwables
import no.octopod.cinema.kino.converter.TheaterConverter
import no.octopod.cinema.common.dto.TheaterDto
import no.octopod.cinema.common.utility.ResponseUtil.getWrappedResponse
import no.octopod.cinema.kino.repository.TheaterRepository
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.http.MediaType
import no.octopod.cinema.common.dto.WrappedResponse
import no.octopod.cinema.kino.entity.TheaterEntity
import io.swagger.annotations.ApiParam
import no.octopod.cinema.common.hateos.Format
import no.octopod.cinema.common.hateos.HalLink
import no.octopod.cinema.common.hateos.HalPage
import no.octopod.cinema.common.utility.ResponseUtil
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

            @ApiParam("ShowEntity dto")
            @RequestBody dto: TheaterDto

    ): ResponseEntity<WrappedResponse<Void>> {

        if (dto.name == null || dto.seats == null) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Missing name or seats"
            )
        }

        val theater = TheaterEntity(name = dto.name!!, seatsMax = dto.seats!!.size)
        theater.seats!!.addAll(dto.seats!!)

        val created = repo.save(theater)

        return ResponseEntity.created(
                UriComponentsBuilder
                        .fromPath("/theaters/${created.id!!}")
                        .build()
                        .toUri()
        ).body(WrappedResponse(
                code = 201,
                message = "Theater Created"
        ))
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

            @ApiParam("TheaterEntity id")
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
            return getWrappedResponse(
                    rawStatusCode = 404,
                    message = "Theater not found"
            )
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

            @ApiParam("TheaterEntity id")
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
            return getWrappedResponse(
                    rawStatusCode = 404,
                    message = "Theater not found"
            )
        }

        repo.deleteById(pathId)

        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Update a theater")
    @PutMapping(path = ["/{id}"])
    fun updateTheater(

            @ApiParam("TheaterEntity id")
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
            return getWrappedResponse(
                    rawStatusCode = 404,
                    message = "Theater not found"
            )
        }

        if (dto.name == null || dto.seats == null) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Missing required keys name and seats"
            )
        }

        if (dtoId != pathId) {
            return getWrappedResponse(
                    rawStatusCode = 409,
                    message = "Path ID and object ID does not match"
            )
        }

        if (!repo.existsById(dtoId)) {
            return getWrappedResponse(
                    rawStatusCode = 404,
                    message = "Theater does not exist"
            )
        }

        val theater = TheaterConverter.transform(dto)

        try {
            repo.save(theater)
        } catch (e: Exception) {
            if(Throwables.getRootCause(e) is ConstraintViolationException) {
                return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "Constraints violated"
                )
            }
            throw e
        }

        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Update a field in a theater")
    @PatchMapping(path = ["/{id}"])
    fun patchTheater(

            @ApiParam("TheaterEntity id")
            @PathVariable("id")
            id: String,

            @ApiParam("New theater JSON")
            @RequestBody
            json: String

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
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Malformed JSON supplied"
            )
        }

        if (jsonNode.has("id")) {
            //shouldn't be allowed to modify the counter id
            return getWrappedResponse(
                    rawStatusCode = 409,
                    message = "Merge patch does not support replacing id"
            )
        }

        var newName = originalDto.name
        var newSeatsMax = originalDto.seatsMax
        var newSeats = originalDto.seats

        if (jsonNode.has("name")) {
            val nameNode = jsonNode.get("name")
            newName = when {
                nameNode.isNull -> return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "Name can not be null"
                )
                nameNode.isTextual -> nameNode.asText()
                else -> //Invalid JSON. Non-string name
                    return getWrappedResponse(
                            rawStatusCode = 404,
                            message = "Name is of invalid type"
                    )
            }
        }

        if (jsonNode.has("seatsMax")) {
            val maxNode = jsonNode.get("seatsMax")
            newSeatsMax = when {
                maxNode.isNull -> return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "Max Seats can not be null"
                )
                maxNode.isNumber -> maxNode.asInt()
                else -> //Invalid JSON. Non-int seatsMax
                    return getWrappedResponse(
                            rawStatusCode = 400,
                            message = "maxSeats is of invalid type"
                    )
            }
        }

        if (jsonNode.has("seats")) {
            val seatsNode = jsonNode.withArray("seats")
            if (seatsNode.isNull) return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Seats can not be null - Must be empty array or with items"
            )
            if (seatsNode.isArray) {
                var mutableArr = mutableListOf<String>()
                seatsNode.elements().forEach { it ->
                    mutableArr.add(it.asText())
                }
                newSeats = mutableArr
            } else {
                return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "Seats is of wrong type"
                )
            }
        }

        originalDto.name = newName
        originalDto.seatsMax = newSeatsMax
        originalDto.seats = newSeats

        repo.save(originalDto)
        return ResponseEntity.status(204).build()
    }
}