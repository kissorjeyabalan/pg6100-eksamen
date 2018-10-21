package com.octopod.cinema.ticket.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import java.time.ZonedDateTime

@Entity
class Ticket (

        @get:NotBlank
        var buyer: String,

        @get:NotBlank
        var movieName: String,

        @get:NotBlank
        var timeOfPurchase: ZonedDateTime,

        @get:NotBlank
        var movieStartTime: ZonedDateTime,

        @get:Id @get:GeneratedValue
        var id: Long? = null
)