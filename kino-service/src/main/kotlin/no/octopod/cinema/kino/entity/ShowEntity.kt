package no.octopod.cinema.kino.entity

import java.time.ZonedDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class ShowEntity (

    @get:NotNull
    var startTime: ZonedDateTime? = null,

    @get:NotNull
    var movieId: Long? = null,

    @get:NotNull
    var cinemaId: Long? = null,

    @get:ElementCollection
    @get:NotNull
    @Column(name = "available_seats")
    var seats: MutableList<String>? = mutableListOf(),

    @get:Id @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)