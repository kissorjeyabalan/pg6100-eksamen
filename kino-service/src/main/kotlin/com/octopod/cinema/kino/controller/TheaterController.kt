package com.octopod.cinema.kino.controller

import com.octopod.cinema.kino.converter.TheaterConverter
import com.octopod.cinema.kino.dto.TheaterDto
import com.octopod.cinema.kino.repository.TheaterRepository
import com.octopod.cinema.kino.service.TheaterService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.http.MediaType
import com.octopod.cinema.common.dto.WrappedResponse
import com.octopod.cinema.kino.entity.Theater

@Api(value = "theaters", description = "Handling theaters")
@RequestMapping(
        path = ["/theaters"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class TheaterController {

    @Autowired
    private lateinit var service: TheaterService

    @Autowired
    lateinit var repo: TheaterRepository

    @ApiOperation("create a new ticket")
    @PostMapping
    fun createTheater(

            @RequestBody dto: TheaterDto

    ): ResponseEntity<Void> {

        if (dto.name == null || dto.seatsMax == null) {
            return ResponseEntity.status(400).build()
        }

        val created = repo.save(Theater(dto.name!!, dto.seatsMax!!, dto.seatsMax!!))

        return ResponseEntity.created(
                UriComponentsBuilder
                        .fromPath("/theaters/${created.id!!}")
                        .build()
                        .toUri()
        ).build()
    }

    @ApiOperation("Get all theaters")
    @GetMapping
    fun getTheaters(

        @RequestParam("limit", defaultValue = "10")
        limit: Int

    ): ResponseEntity<WrappedResponse<List<TheaterDto>>> {

        if (limit < 1) {
            return ResponseEntity.status(400).body(
                    WrappedResponse<List<TheaterDto>>(
                            code = 400,
                            message = "Malformed limit supplied"
                    ).validated()
            )
        }

        /*val entryList = repo.getTheaters(limit).toList()
        val dto = TheaterConverter.transform(entryList, limit)*/
        val entryList = repo.findAll().toList()
        val dto = TheaterConverter.transform(entryList, 100)

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

            @PathVariable("id")
            id: String

    ): ResponseEntity<WrappedResponse<TheaterDto>> {

        //Finne ut om dette er riktig måte å gjøre det fra long til string og
        val pathId: Long
        try {
            pathId = id!!.toLong()
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

            @PathVariable("id")
            id: String

    ) : ResponseEntity<WrappedResponse<TheaterDto>> {

        val pathId: Long
        try {
            pathId = id!!.toLong()
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
}