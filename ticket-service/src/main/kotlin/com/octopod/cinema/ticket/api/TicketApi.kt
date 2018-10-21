package com.octopod.cinema.ticket.api

import com.octopod.cinema.ticket.dto.DtoTransformer
import com.octopod.cinema.ticket.dto.TicketDto
import com.octopod.cinema.ticket.entity.Ticket
import com.octopod.cinema.ticket.hal.HalLink
import com.octopod.cinema.ticket.hal.PageDto
import com.octopod.cinema.ticket.service.TicketService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@Api(value = "tickets", description = "handling of tickets")
@RequestMapping(
        path = ["/tickets"]
)
@RestController
class TicketApi {

    @Autowired
    private lateinit var service: TicketService


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

    ): ResponseEntity<PageDto<TicketDto>> {

        if (offset < 0 || limit < 1) {
            return ResponseEntity.status(400).build()
        }

        val maxFromDB = 50

        val ticketList: List<Ticket>


        ticketList = if( screeningId.isNullOrBlank() && userId.isNullOrBlank()) {
            println("dwadwa")
            service.getTickets(maxFromDB)

        } else if ( !screeningId.isNullOrBlank() && !userId.isNullOrBlank()) {
            service.getTicketsByScreeningIdAndUserId(maxFromDB, userId!!, screeningId!!)
        } else {
            service.getTicketsByUserId(maxFromDB, userId!!)
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

        return ResponseEntity.ok(dto)
    }


    @ApiOperation("create a new ticket")
    @PostMapping
    fun createTicket(@RequestBody dto: TicketDto) : ResponseEntity<Void> {

        if(dto.userId == null || dto.movieName == null || dto.screeningId == null || dto.movieStartTime == null) {
            return ResponseEntity.status(400).build()
        }

        val id = service.createTicket(dto.userId!!, dto.movieName!!, dto.screeningId!!, dto.movieStartTime!!)

        return ResponseEntity.created(UriComponentsBuilder
                .fromPath("/tickets/$id").build().toUri()
        ).build()
    }

}


