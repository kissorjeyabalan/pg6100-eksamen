package no.octopod.cinema.kino.entity

import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class TheaterEntity (

        @get:NotBlank
        var name: String? = null,

        @get:NotNull
        var seatsMax: Int? = null,

        @get:ElementCollection
        @get:NotNull
        var seats: MutableList<String>? = mutableListOf(),

        @get:Id @get:GeneratedValue
        var id: Long? = null
)