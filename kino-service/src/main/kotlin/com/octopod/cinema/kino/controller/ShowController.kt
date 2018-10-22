package com.octopod.cinema.kino.controller

import com.octopod.cinema.kino.dto.ShowDto
import com.octopod.cinema.kino.service.ShowService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@Api(value = "shows", description = "handling of shows")
@RequestMapping(
        path = ["/shows"]
)

@RestController
class ShowController {

    @Autowired
    private lateinit var service : ShowService

    @ApiOperation("create a new show")
    @PostMapping
    fun createShow(@RequestBody dto : ShowDto) : ResponseEntity<Void> {

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
}