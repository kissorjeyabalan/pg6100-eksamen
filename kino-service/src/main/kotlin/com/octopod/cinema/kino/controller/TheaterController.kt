package com.octopod.cinema.kino.controller

import com.octopod.cinema.kino.dto.TheaterDto
import com.octopod.cinema.kino.service.TheaterService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import javax.xml.ws.Service

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
}