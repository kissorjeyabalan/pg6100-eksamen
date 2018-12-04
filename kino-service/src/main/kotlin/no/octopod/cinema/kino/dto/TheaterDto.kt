package no.octopod.cinema.kino.dto

import io.swagger.annotations.ApiModelProperty

data class TheaterDto (

        @ApiModelProperty("Name of the theater")
        var name: String? = null,

        @ApiModelProperty("Number of seats in the theater")
        var seatsMax: Int? = null,

        @ApiModelProperty("Id for the theater")
        var id: Long? = null,

        var seats: List<String>? = null
)