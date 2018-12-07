package no.octopod.cinema.kino.entity

import javax.persistence.*
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

    @get:ElementCollection
    @get:NotNull
    @Column(name = "available_seats")
    var seats: MutableList<String>? = mutableListOf(),

    @get:Id @get:GeneratedValue
    var id: Long? = null

)