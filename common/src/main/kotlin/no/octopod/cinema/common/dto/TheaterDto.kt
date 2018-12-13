package no.octopod.cinema.common.dto

import io.swagger.annotations.ApiModelProperty

data class TheaterDto (

        @ApiModelProperty("Name of the theater")
        var name: String? = null,

        @ApiModelProperty("Number of seats in the theater")
        var seatsMax: Int? = null,

        @ApiModelProperty("Id for the theater")
        var id: Long? = null,

        @ApiModelProperty("Seats in the theater")
        var seats: MutableList<String>? = null
)