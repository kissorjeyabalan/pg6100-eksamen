package com.octopod.cinema.ticket.api

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.octopod.cinema.ticket.dto.DtoTransformer
import com.octopod.cinema.common.dto.TicketDto
import com.octopod.cinema.common.dto.WrappedResponse
import com.octopod.cinema.ticket.entity.Ticket
import com.octopod.cinema.ticket.hal.HalLink
import com.octopod.cinema.ticket.hal.PageDto
import com.octopod.cinema.ticket.repository.TicketRepository
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@Api(value = "tickets", description = "handling of tickets")
@RequestMapping(
        path = ["/tickets"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class TicketApi {

    @Autowired
    private lateinit var repo: TicketRepository

    @ApiOperation("Get all tickets")
    @GetMapping
    fun getTickets(

            @ApiParam("the id of the Screening")
            @RequestParam("screeningId", required = false)
            screeningId: String?,

            @ApiParam("the name of the user who bought the ticket")
            @RequestParam("userId", required = false)
            userId: String?,

            @ApiParam("offset")
            @RequestParam("offset", defaultValue = "0")
            offset: Int,

            @ApiParam("Limit of tickets in a single retrieved page")
            @RequestParam("limit", defaultValue = "10")
            limit: Int

    ): ResponseEntity<WrappedResponse<PageDto<TicketDto>>> {

        if (offset < 0 || limit < 1) {
            return ResponseEntity.status(400).build()
        }

        val ticketList: List<Ticket>

        ticketList = if( screeningId.isNullOrBlank() && userId.isNullOrBlank()) {
            repo.findAll().toList()

        } else if ( !screeningId.isNullOrBlank() && !userId.isNullOrBlank()) {
            repo.findAllByScreeningIdAndUserId(userId!!, screeningId!!)
        } else {
            repo.findAllByUserId(userId!!)
        }


        if (offset != 0 && offset >= ticketList.size) {
            return ResponseEntity.status(400).build()
        }

        val dto = DtoTransformer.transform(
                ticketList, offset, limit)

        var builder = UriComponentsBuilder
                .fromPath("/tickets")
                .queryParam("limit", limit)


        dto._self = HalLink(builder.cloneBuilder()
                .queryParam("offset", offset)
                .build().toString()
        )

        if (!ticketList.isEmpty() && offset > 0) {
            dto.previous = HalLink(builder.cloneBuilder()
                    .queryParam("offset", Math.max(offset - limit, 0))
                    .build().toString()
            )
        }

        if (offset + limit < ticketList.size) {
            dto.next = HalLink(builder.cloneBuilder()
                    .queryParam("offset", offset + limit)
                    .build().toString())
        }

        return ResponseEntity.ok(WrappedResponse(
                code = 200,
                data = dto
        ).validated())
    }

    @ApiOperation("create a new ticket")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createTicket(@RequestBody dto: TicketDto) : ResponseEntity<Void> {

        if(dto.userId == null || dto.screeningId == null) {
            return ResponseEntity.status(400).build()
        }

        val id = repo.createTicket(dto.userId!!, dto.screeningId!!)

        //TODO wrap response
        return ResponseEntity.created(UriComponentsBuilder
                .fromPath("/tickets/$id").build().toUri()
        ).build()
    }

    @ApiOperation("delete a ticket")
    @DeleteMapping(path = ["/{id}"])
    fun deleteTicket(@PathVariable("id") ticketId: String? ) : ResponseEntity<WrappedResponse<TicketDto>> {

        val id: Long
        try {
            id = ticketId!!.toLong()
        } catch (e: Exception) {
            return ResponseEntity.status(400).body(
                    WrappedResponse<TicketDto>(
                            code = 400,
                            message = "Id is missing or malformed"
                    ).validated()
            )
        }

        if (!repo.existsById(id)) {
            return ResponseEntity.status(404).body(
                    WrappedResponse<TicketDto>(
                            code = 404,
                            message = "No entity with given id exists"
                    ).validated()
            )
        }

        repo.deleteById(id)

        return ResponseEntity.status(204).body(
                WrappedResponse<TicketDto>(
                        code = 204
                ).validated()
        )
    }

    @ApiOperation("update an existing ticket")
    @PutMapping(path = ["/{id}"])
    fun update(
            @ApiParam("The id of the ticket to be updated")
            @PathVariable("id")
            pathId: String,
            @ApiParam("The new ticket values")
            @RequestBody
            dto: TicketDto
    ) : ResponseEntity<WrappedResponse<TicketDto>> {

        if (!repo.existsById(dto.id!!.toLong())) {
            return ResponseEntity.status(404).body(
                    WrappedResponse<TicketDto>(
                            code = 404,
                            message = "No entity with given id exists"
                    ).validated()
            )
        }

        if (dto.userId == null || dto.screeningId == null || dto.timeOfPurchase == null) {
            return ResponseEntity.status(400).body(
                    WrappedResponse<TicketDto>(
                            code = 400,
                            message = "Id is missing or malformed"
                    ).validated()
            )
        }

        repo.updateTicket(dto.id!!.toLong(), dto.userId!!, dto.screeningId!!, dto.timeOfPurchase!!)

        return ResponseEntity.status(204).body(
                WrappedResponse<TicketDto>(
                        code = 204
                ).validated()
        )
    }

    @ApiOperation("Modify the fields of a ticket")
    @PatchMapping( path = ["/{id}"])
    fun mergePatch( @ApiParam("the id of the ticket")
                    @PathVariable("id")
                    id: Long?,
                    @ApiParam("The partial patch")
                    @RequestBody
                    jsonPatch: String
    ) : ResponseEntity<WrappedResponse<TicketDto>> {

        val jackson = ObjectMapper()

        val jsonNode: JsonNode

        return ResponseEntity.status(400).body(
                WrappedResponse<TicketDto>(
                        code = 400,
                        message = "not finished here yet"
                ).validated()
        )
    }
}


