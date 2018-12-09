package no.octopod.cinema.kino.dto

import io.swagger.annotations.ApiModelProperty
import java.time.ZonedDateTime

data class ShowDto (

        @ApiModelProperty("The time of when the show starts, standard ISO format")
        var startTime: ZonedDateTime? = null,

        @ApiModelProperty("Id of the movie")
        var movieId: Long? = null,

        @ApiModelProperty("Id of the hosting theater")
        var cinemaId: Long? = null,

        @ApiModelProperty("Available seats in show")
        var availableSeats: MutableList<String>? = null,

        @ApiModelProperty("Id for the show")
        var id: Long? = null
)