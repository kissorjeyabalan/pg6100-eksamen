package no.octopod.cinema.kino.dto

import io.swagger.annotations.ApiModelProperty
import org.springframework.format.annotation.DateTimeFormat
import java.time.ZonedDateTime

data class ShowDto (

        //TODO: change to dateTime
        @ApiModelProperty("The time when the show starts")
        var startTime: ZonedDateTime? = null,

        @ApiModelProperty("Name of the movie")
        var movieId: Long? = null,

        @ApiModelProperty("Name of the hosting cinema")
        var cinemaId: Long? = null,

        @ApiModelProperty("Available seats in show")
        var availableSeats: MutableList<String>? = null,

        @ApiModelProperty("Id for the show")
        var id: Long? = null
)