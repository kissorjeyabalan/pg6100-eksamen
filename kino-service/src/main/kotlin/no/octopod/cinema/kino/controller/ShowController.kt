package no.octopod.cinema.kino.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import no.octopod.cinema.kino.converter.ShowConverter
import no.octopod.cinema.common.dto.ShowDto
import no.octopod.cinema.common.utility.ResponseUtil.getWrappedResponse
import no.octopod.cinema.kino.repository.ShowRepository
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import no.octopod.cinema.common.dto.WrappedResponse
import no.octopod.cinema.kino.entity.ShowEntity
import io.swagger.annotations.ApiParam
import no.octopod.cinema.common.hateos.Format
import no.octopod.cinema.common.hateos.HalLink
import no.octopod.cinema.common.hateos.HalPage
import no.octopod.cinema.kino.repository.TheaterRepository
import org.springframework.http.MediaType
import java.time.ZonedDateTime

@Api(value = "shows", description = "Handling of shows")
@RequestMapping(
        path = ["/shows"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)

@RestController
class ShowController {

    @Autowired
    lateinit var repo: ShowRepository

    @Autowired
    lateinit var theaterRepo: TheaterRepository

    @ApiOperation("Create a new show")
    @PostMapping
    fun createShow(

            @ApiParam("ShowEntity dto")
            @RequestBody dto: ShowDto

    ): ResponseEntity<WrappedResponse<Void>> {

        if (dto.movieId == null || dto.cinemaId == null || dto.startTime == null) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Invalid request body supplied"
            )
        }

        val cinemaId: Long
        try {
            cinemaId = dto.cinemaId!!
        } catch (e: Exception) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Invalid cinema ID supplied"
            )
        }

        val theater = theaterRepo.findById(cinemaId).orElse(null)
                ?: return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "Supplied theater does not exist"
                )

        val showEntity = ShowEntity(startTime = dto.startTime!!.withFixedOffsetZone().withNano(0), movieId = dto.movieId!!, cinemaId = theater.id, seats = theater.seats!!.toMutableList())

        val created = repo.save(showEntity)

        return ResponseEntity.created(
                UriComponentsBuilder
                        .fromPath("/shows/${created.id}")
                        .build()
                        .toUri()
        ).body(WrappedResponse<Void>(code = 201, message = "Show created").validated())
    }

    @ApiOperation("Get all shows")
    @GetMapping(produces = [Format.HAL_V1])
    fun getShows(

            @ApiParam("Page number")
            @RequestParam("page", defaultValue = "1")
            page: String,

            @ApiParam("Limit")
            @RequestParam("limit", defaultValue = "10")
            limit: String,

            @ApiParam("TheaterEntity id")
            @RequestParam("theater", required = false)
            theater: String?,

            @ApiParam("Movie Id")
            @RequestParam("movie", required = false)
            movie: String?

    ): ResponseEntity<WrappedResponse<HalPage<ShowDto>>> {

        val pageInt = page.toInt()
        val limitInt = limit.toInt()

        if (pageInt < 1 || limitInt < 1) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Malformed limit supplied"
            )
        }

        val entryList: List<ShowEntity> =
        if ( !theater.isNullOrBlank() && !movie.isNullOrBlank() ) {

            val theaterId: Long
            val movieId: Long
            try {
                theaterId = theater!!.toLong()
                movieId = movie!!.toLong()
            } catch (e: Exception) {
                return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "Invalid theater ID or movie ID supplied"
                )
            }
            repo.findAllByCinemaIdAndMovieId(theaterId, movieId)

        } else if ( !theater.isNullOrBlank() && movie.isNullOrBlank() ) {

            val theaterId: Long
            try {
                theaterId = theater!!.toLong()
            } catch (e: Exception) {
                return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "Invalid theater ID supplied"
                )
            }
            repo.findAllByCinemaId(theaterId)

        } else {
            repo.findAll().toList()
        }

        val dto = ShowConverter.transform(entryList, pageInt, limitInt)

        val uriBuilder = UriComponentsBuilder.fromPath("/shows")
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

        return getWrappedResponse(
                rawStatusCode = 200,
                data = dto
        )
    }

    @ApiOperation("Get show with specific id")
    @GetMapping(path = ["/{id}"])
    fun getShow(

            @ApiParam("ShowEntity id")
            @PathVariable("id")
            id: String

    ): ResponseEntity<WrappedResponse<ShowDto>> {

        val pathId: Long
        try {
            pathId = id.toLong()
        } catch (e: Exception) {
            return getWrappedResponse(
                    rawStatusCode = 404,
                    message = "Show not found"
            )
        }

        val entity = repo.findById(pathId).orElse(null) ?:
                return getWrappedResponse(
                        rawStatusCode = 404,
                        message = "Show not found"
                )
        val dto = ShowConverter.transform(entity)

        return getWrappedResponse(
                rawStatusCode = 200,
                data = dto
        )
    }

    @ApiOperation("Delete show with specific id")
    @DeleteMapping(path = ["/{id}"])
    fun deleteShowById(

            @ApiParam("ShowEntity id")
            @PathVariable("id")
            id: String

    ): ResponseEntity<WrappedResponse<ShowDto>> {

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
                    message = "Show not found"
            )
        }

        repo.deleteById(pathId)

        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Delete specific seats from show")
    @DeleteMapping(path= ["/{id}/seats/{seatId}"])
    fun deleteSeatFromShow(

            @ApiParam("ShowEntity id")
            @PathVariable("id")
            id: String,

            @ApiParam("seat number")
            @PathVariable("seatId")
            seatId: String

    ): ResponseEntity<WrappedResponse<ShowDto>> {

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
                    message = "Show not found"
            )
        }

        val showEntity = repo.findById(pathId).orElse(null)
        val seatExists = showEntity?.seats?.contains(seatId)

        if (showEntity == null || seatExists == null || !seatExists) {
            return getWrappedResponse(
                    rawStatusCode = 404,
                    message = "Show or Seat does not exist"
            )
        }

        showEntity.seats!!.remove(seatId)

        repo.save(showEntity)

        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Add specific seat to show")
    @PostMapping(path = ["/{id}/seats/{seatId}"])
    fun postSeatInShow(

            @ApiParam("ShowEntity id")
            @PathVariable("id")
            id: String,

            @ApiParam("seat number")
            @PathVariable("seatId")
            seatId: String

    ): ResponseEntity<WrappedResponse<ShowDto>> {

        val pathId: Long
        try {
            pathId = id.toLong()
        } catch (e: Exception) {
            return getWrappedResponse(
                    rawStatusCode = 404,
                    message = "Show not found"
            )
        }

        val showEntity = repo.findById(pathId).orElse(null)?: return getWrappedResponse(
                rawStatusCode = 404,
                message = "Show not found"
        )

        val theater = theaterRepo.findById(showEntity.cinemaId!!).orElse(null)?: return getWrappedResponse(
                rawStatusCode = 500,
                message = "Internal Server Error"
        )

        if (!theater.seats!!.contains(seatId)) return getWrappedResponse(
                rawStatusCode = 400,
                message = "Seat already belongs to show"
        )

        val seatExists = showEntity.seats?.contains(seatId) ?: return getWrappedResponse(
                rawStatusCode = 404,
                message = "Show does not have supplied seat"
        )

        if (seatExists) {
            return getWrappedResponse(
                    rawStatusCode = 409,
                    message = "Show already has this seat"
            )
        }

        showEntity.seats!!.add(seatId)

        repo.save(showEntity)

        return ResponseEntity.status(204).build()
    }


    @ApiOperation("Update a show with specific id")
    @PutMapping(path = ["/{id}"])
    fun updateShow(

            @ApiParam("ShowEntity id")
            @PathVariable("id")
            id: String,

            @ApiParam("New show dto")
            @RequestBody
            dto: ShowDto

    ): ResponseEntity<WrappedResponse<ShowDto>> {

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
                    message = "Show not found"
            )
        }

        if (dtoId != pathId) {
            return getWrappedResponse(
                    rawStatusCode = 409,
                    message = "Path ID conflicts with body ID"
            )
        }

        if (!repo.existsById(dtoId)) {
            return getWrappedResponse(
                    rawStatusCode = 404,
                    message = "Show not found"
            )
        }

        if (dto.startTime == null || dto.cinemaId == null || dto.movieId == null) {
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "Missing start time, cinemaId or movieId"
            )
        }

        dto.startTime = dto.startTime!!.withFixedOffsetZone().withNano(0)
        val show = ShowConverter.transform(dto)

        repo.save(show)

        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Update a field in a show with specific id")
    @PatchMapping(path = ["/{id}"], consumes = ["application/merge-patch+json"])
    fun patchShow(

            @ApiParam("ShowEntity id")
            @PathVariable("id")
            id: String,

            @ApiParam("New show JSON")
            @RequestBody
            json: String

    ): ResponseEntity<WrappedResponse<ShowDto>> {

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
                    message = "Show not found"
            )
        }

        val originalDto = repo.findById(pathId).orElse(null) ?: return ResponseEntity.status(404).body(
                WrappedResponse<ShowDto>(
                        code = 404,
                        message = "Resource not found"
                ).validated()
        )

        //taken from arcuri82 CounterRestPatch
        // https://stackoverflow.com/questions/39086472/jackson-serializes-a-zoneddatetime-wrongly-in-spring-boot
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        val jsonNode: JsonNode
        try {
            jsonNode = mapper.readValue(json, JsonNode::class.java)
        } catch (e: Exception) {
            //Invalid JSON data as input
            return getWrappedResponse(
                    rawStatusCode = 400,
                    message = "JSON Malformed"
            )
        }

        if (jsonNode.has("id")) {
            //shouldn't be allowed to modify the counter id
            return getWrappedResponse(
                    rawStatusCode = 409,
                    message = "Merge patch must not contain ID"
            )
        }

        var newStartTime = originalDto.startTime
        var newCinemaId = originalDto.cinemaId
        var newMovieId = originalDto.movieId
        var newSeats = originalDto.seats

        if (jsonNode.has("startTime")) {
            val startTimeNode = jsonNode.get("startTime")
            newStartTime = when {
                startTimeNode.isNull -> return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "startTime can not be deleted"
                )
                startTimeNode.isTextual ->
                    try {
                        ZonedDateTime.parse(startTimeNode.asText()).withFixedOffsetZone().withNano(0)
                    } catch (e: Exception) {
                        return getWrappedResponse(
                                rawStatusCode = 400,
                                message = "startTime is wrongly formatted"
                        )
                    }
                else -> //Invalid JSON. Non-Integer startTime
                    return getWrappedResponse(
                            rawStatusCode = 400,
                            message = "Invalid node for startTime"
                    )
            }
        }

        //TODO: check if cinema exists
        if (jsonNode.has("cinemaId")) {
            val cinemaIdNode = jsonNode.get("cinemaId")
            newCinemaId = when {
                cinemaIdNode.isNull -> return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "cinemaId can not be deleted"
                )
                cinemaIdNode.isInt -> cinemaIdNode.asInt().toLong()
                else -> //Invalid JSON. Non-Long cinemaId
                     return getWrappedResponse(
                            rawStatusCode = 400,
                            message = "Invalid node for cinemaId"
                    )
            }
        }

        if (jsonNode.has("movieId")) {
            val movieIdNode = jsonNode.get("movieId")
            newMovieId = when {
                movieIdNode.isNull -> return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "Missing start time, cinemaId or movieId"
                )
                movieIdNode.isInt -> movieIdNode.asInt().toLong()
                else -> //Invalid JSON. Non-Long movieId
                    return getWrappedResponse(
                            rawStatusCode = 400,
                            message = "Invalid node for movieId"
                    )
            }
        }

        if (jsonNode.has("availableSeats")) {
            val seatsNode = jsonNode.get("availableSeats")

            if (seatsNode.isNull) {
                return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "Missing seatsNode"
                )
            } else if (seatsNode.isArray) {
                newSeats = mutableListOf()
                for (seatNode in seatsNode) {
                    if (seatNode.isTextual) {
                        newSeats.add(seatNode.asText())
                    }
                }
            } else {
                return getWrappedResponse(
                        rawStatusCode = 400,
                        message = "Invalid item in seats array"
                )
            }
        }

        originalDto.startTime = newStartTime
        originalDto.cinemaId = newCinemaId
        originalDto.movieId = newMovieId
        originalDto.seats = newSeats

        repo.save(originalDto)
        return ResponseEntity.status(204).build()
    }
}