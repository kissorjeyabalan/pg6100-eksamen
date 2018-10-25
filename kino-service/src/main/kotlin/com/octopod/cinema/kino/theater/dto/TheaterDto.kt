package com.octopod.cinema.kino.show.dto

import io.swagger.annotations.ApiModelProperty

data class TheaterDto (

        @ApiModelProperty("Name of the theater")
        var name: String,

        @ApiModelProperty("Number of seats in the theater")
        var seatsMax: Int,

        @ApiModelProperty("Current number of empty seats")
        var seatsEmpty: Int,

        @ApiModelProperty("Id for the theater")
        var id: String? = null
)