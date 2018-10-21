package com.octopod.cinema.ticket.dto

import io.swagger.annotations.ApiModelProperty
import java.time.ZonedDateTime

data class TicketDto (

        @ApiModelProperty("The name of the person who bought the ticket")
        var userId: String? = null,

        @ApiModelProperty("The name of the movie being shown")
        var movieName: String? = null,

        @ApiModelProperty("The id of the screening")
        var screeningId: String? = null,

        @ApiModelProperty("The time when the ticket was bought")
        var timeOfPurchase: ZonedDateTime? = null,

        @ApiModelProperty("The start time of the movie")
        var movieStartTime: ZonedDateTime? = null,

        @ApiModelProperty("The id of the ticket")
        var id: String? = null
)