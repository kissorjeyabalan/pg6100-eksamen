package no.octopod.cinema.ticket.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import java.time.ZonedDateTime
import javax.validation.constraints.NotNull

@Entity
class Ticket (

        @get:NotBlank
        var userId: String,

        @get:NotBlank
        var screeningId: String,

        @get:NotNull
        var timeOfPurchase: ZonedDateTime,

        @get:Id @get:GeneratedValue
        var id: Long? = null
)