package no.octopod.cinema.kino.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class ShowEntity (

        @get:NotNull
        var startTime: Int? = null,

        @get:NotNull
        var movieId: Long? = null,

        @get:NotNull
        var cinemaId: Long? = null,

        @get:Id @get:GeneratedValue
        var id: Long? = null

)