package com.octopod.cinema.kino.controller

import com.octopod.cinema.kino.converter.ShowConverter
import com.octopod.cinema.kino.dto.ShowDto
import com.octopod.cinema.kino.service.ShowService
import com.octopod.cinema.common.dto.WrappedResponse
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@Api(value = "shows", description = "handling of shows")
@RequestMapping(
        path = ["/shows"]
)

@RestController
class ShowController {

    @Autowired
    private lateinit var service: ShowService

    @ApiOperation("create a new show")
    @PostMapping
    fun createShow(@RequestBody dto: ShowDto): ResponseEntity<Void> {

        if (dto.id == null || dto.movieName == null || dto.cinemaName == null || dto.startTime == null) {
            return ResponseEntity.status(400).build()
        }

        val id = service.createShow(dto.startTime!!, dto.movieName!!, dto.cinemaName!!)

        return ResponseEntity.created(
                UriComponentsBuilder
                        .fromPath("/shows/$id")
                        .build()
                        .toUri()
        ).build()
    }

    @ApiOperation("Get all shows")
    @GetMapping
    fun getShows(

            @RequestParam("limit", defaultValue = "10")
            limit: Int

    ): ResponseEntity<WrappedResponse<List<ShowDto>>> {

        if (limit < 1) {
            return ResponseEntity.status(400).body(
                    WrappedResponse<List<ShowDto>>(
                            code = 400,
                            message = "Malformed limit supplied"
                    ).validated()
            )
        }

        val entryList = service.getShows(limit).toList()
        val dto = ShowConverter.transform(entryList, limit)

        return ResponseEntity.ok(
                WrappedResponse(
                        code = 200,
                        data = dto
                ).validated()
        )
    }

    @ApiOperation("Get show with specific id")
    @GetMapping(path = ["/{id}"])
    fun getShow(

            @PathVariable("id")
            id: String

    ): ResponseEntity<WrappedResponse<ShowDto>> {

        val entryObject = service.getShow(id.toLong())
        val dto = ShowConverter.transform(entryObject)

        return ResponseEntity.ok(
                WrappedResponse(
                        code = 200,
                        data = dto
                ).validated()
        )
    }

    @ApiOperation("Get all shows for a specific theater")
    @GetMapping(path = ["/{theater}"])
    fun getShowsByTheater(

            @RequestParam("limit", defaultValue = "10")
            limit: Int,

            @PathVariable("theater")
            theater: String

    ): ResponseEntity<WrappedResponse<List<ShowDto>>> {

        if (limit < 1) {
            return ResponseEntity.status(400).body(
                    WrappedResponse<List<ShowDto>>(
                            code = 400,
                            message = "Malformed limit supplied"
                    ).validated()
            )
        }

        val entryList = service.getShowsByTheater(limit, theater).toList()
        val dto = ShowConverter.transform(entryList, limit)

        return ResponseEntity.ok(
                WrappedResponse(
                        code = 200,
                        data = dto
                ).validated()
        )
    }
}