package no.octopod.cinema.ticket.api

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.octopod.cinema.ticket.dto.DtoTransformer
import no.octopod.cinema.common.dto.TicketDto
import no.octopod.cinema.common.dto.WrappedResponse
import no.octopod.cinema.ticket.repository.TicketRepository
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.octopod.cinema.common.hateos.HalLink
import no.octopod.cinema.common.hateos.HalPage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.security.Principal

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

            @ApiParam("Page number")
            @RequestParam("page", defaultValue = "1")
            page: String,

            @ApiParam("Limit of tickets in a single retrieved page")
            @RequestParam("limit", defaultValue = "10")
            limit: Int

    ): ResponseEntity<WrappedResponse<HalPage<TicketDto>>> {


        val pageInt = page.toInt()
        val limitInt = limit.toInt()

        if (pageInt < 1 || limitInt < 1) {
            return ResponseEntity.status(400).body(
                    WrappedResponse<HalPage<TicketDto>>(
                            code = 400,
                            message = "Malformed limit og page number supplied"
                    ).validated()
            )
        }

        val ticketList = if( screeningId.isNullOrBlank() && userId.isNullOrBlank()) {
            repo.findAll().toList()

        } else if ( !screeningId.isNullOrBlank() && !userId.isNullOrBlank()) {
            repo.findAllByScreeningIdAndUserId(userId!!, screeningId!!)
        } else {
            repo.findAllByUserId(userId!!)
        }

        val dto = DtoTransformer.transform(
                ticketList, pageInt, limit)

        var builder = UriComponentsBuilder
                .fromPath("/tickets")


        dto._self = HalLink(builder.cloneBuilder()
                .queryParam("page", page)
                .queryParam("limit", limit)
                .build().toString()
        )

        if (!ticketList.isEmpty() && pageInt > 0) {
            dto.previous = HalLink(builder.cloneBuilder()
                    .queryParam("page", (pageInt - 1).toString())
                    .queryParam("limit", limit)
                    .build().toString()
            )
        }

        if (((pageInt) * limitInt) < ticketList.size) {
            dto.next = HalLink(builder.cloneBuilder()
                    .queryParam("page", (pageInt + 1).toString())
                    .queryParam("limit", limit)
                    .build().toString())
        }

        return ResponseEntity.ok(WrappedResponse(
                code = 200,
                data = dto
        ).validated())
    }

    @ApiOperation("Get a single ticket by id")
    @GetMapping(path = ["/{id}"])
    fun getTicket(
            @ApiParam("Ticket id")
            @PathVariable("id")
            id: String

    ): ResponseEntity<WrappedResponse<TicketDto>> {
        val pathId: Long
        try {
            pathId = id.toLong()
        } catch (e: Exception) {
            return ResponseEntity.status(404).build()
        }

        val entity = repo.findById(pathId).orElse(null) ?: return ResponseEntity.status(404).build()
        val dto = DtoTransformer.transform(entity)

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




    // Copied from CounterRest class in package org.tsdes.advanced.rest.patch
    @ApiOperation("Modify the fields of a ticket")
    @PatchMapping( path = ["/{id}"],
                   consumes = ["application/merge-patch+json"])
    fun mergePatch( @ApiParam("the id of the ticket")
                    @PathVariable("id")
                    id: Long?,
                    @ApiParam("The partial patch")
                    @RequestBody
                    jsonPatch: String
    ) : ResponseEntity<WrappedResponse<TicketDto>> {

        if (!repo.existsById(id!!)) {
            return ResponseEntity.status(404).body(
                    WrappedResponse<TicketDto>(
                            code = 404,
                            message = "No entity with given id exists"
                    ).validated()
            )
        }

        val ticketOptional = repo.findById(id)
        val ticket = ticketOptional.get()
        val ticketDto = DtoTransformer.transform(ticket)

        val jackson = ObjectMapper()

        val jsonNode: JsonNode

        try {
            jsonNode = jackson.readValue(jsonPatch, JsonNode::class.java)
        } catch (e: Exception) {
            //Invalid JSON data as input
            return ResponseEntity.status(400).build()
        }

        if (jsonNode.has("id")) {
            //shouldn't be allowed to modify the counter id
            return ResponseEntity.status(409).build()
        }

        //do not alter dto till all data is validated. A PATCH has to be atomic,
        //either all modifications are done, or none.
        var newUserId = ticketDto.userId
        var newScreeningId = ticketDto.screeningId

        if (jsonNode.has("userId")) {
            val userIdNode = jsonNode.get("userId")
            if (userIdNode.isNull) {
                newUserId = null
            } else if (userIdNode.isTextual) {
                newUserId = userIdNode.asText()
            } else {
                //Invalid JSON. Non-string name
                return ResponseEntity.status(400).build()
            }
        }

        if (jsonNode.has("screeningId")) {
            val screeningIdNode = jsonNode.get("screeningId")
            if (screeningIdNode.isNull) {
                newScreeningId = null
            } else if (screeningIdNode.isTextual) {
                newScreeningId = screeningIdNode.asText()
            } else {
                //Invalid JSON. Non-string name
                return ResponseEntity.status(400).build()
            }
        }



        //now that the input is validated, do the update
        ticketDto.userId = newUserId
        ticketDto.screeningId = newScreeningId


        return ResponseEntity.status(204).body(
                WrappedResponse<TicketDto>(
                        code = 204
                ).validated()
        )
    }

    private fun isAuthenticatedOrAdmin(authentication: Authentication, id: String): Boolean {
        var principal = authentication.principal as Principal
        val userDetails = principal as UserDetails
        if (userDetails.username == id ||
                authentication.authorities.contains(SimpleGrantedAuthority("ADMIN"))) {
            return true
        }
        return false
    }
}


