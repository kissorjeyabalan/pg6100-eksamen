package com.octopod.cinema.kino.controller

import com.octopod.cinema.kino.converter.TheaterConverter
import com.octopod.cinema.kino.dto.TheaterDto
import com.octopod.cinema.kino.service.TheaterService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import javax.xml.ws.Service
import dto.WrappedResponse

@Api(value = "theater", description = "Handling theaters")
@RequestMapping(
        path = ["/theater"]
)
@RestController
class TheaterController {

    @Autowired
    private lateinit var service : TheaterService

    @ApiOperation("create a new ticket")
    @PostMapping
    fun createTheater(@RequestBody dto : TheaterDto) : ResponseEntity<Void> {

        if (dto.id == null || dto.name == null || dto.seatsMax == null) {
            return ResponseEntity.status(400).build()
        }

        val id = service.createTheater(dto.name!!, dto.seatsMax!!, dto.seatsMax!!)

        return ResponseEntity.created(
                UriComponentsBuilder
                .fromPath("/theaters/$id")
                .build()
                .toUri()
        ).build()
    }

    @ApiOperation("Get all theaters")
    @GetMapping
    fun getTheaters(

        @RequestParam("limit", defaultValue = "10")
        limit : Int

    ) : ResponseEntity<WrappedResponse<TheaterDto>> {

        if (limit < 1) {
            return ResponseEntity.status(400).body(
                    WrappedResponse<TheaterDto>(
                            code = 400,
                            message = "Malformed limit supplied"
                    ).validated()
            )
        }

        val entryList = service.getTheaters(limit).toList()
        val dto = TheaterConverter.transform(entryList, limit)

        return ResponseEntity.ok(
                WrappedResponse(
                        code = 200,
                        dara = dto
                ).validated()
        )
    }
}