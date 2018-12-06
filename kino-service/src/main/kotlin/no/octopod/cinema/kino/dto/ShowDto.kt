package no.octopod.cinema.kino.dto

import io.swagger.annotations.ApiModelProperty

data class ShowDto (

        @ApiModelProperty("The time when the show starts")
        var startTime: Int? = null,

        @ApiModelProperty("Name of the movie")
        var movieId: String? = null,

        @ApiModelProperty("Name of the hosting cinema")
        var cinemaId: String? = null,

        var availableSeats: List<String>? = null,

        @ApiModelProperty("Id for the show")
        var id: Long? = null
)