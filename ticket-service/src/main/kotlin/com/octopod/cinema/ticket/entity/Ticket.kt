package com.octopod.cinema.ticket.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
class Ticket (

        @get:NotBlank
        var buyer: String,

        @get:Id @get:GeneratedValue
        var id: Long? = null
)