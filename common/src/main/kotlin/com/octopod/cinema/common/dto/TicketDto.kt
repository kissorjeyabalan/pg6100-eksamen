package com.octopod.cinema.common.dto

import io.swagger.annotations.ApiModelProperty
import java.time.ZonedDateTime

data class TicketDto (

        @ApiModelProperty("The name of the person who bought the ticket")
        var userId: String? = null,

        @ApiModelProperty("The id of the screening")
        var screeningId: String? = null,

        @ApiModelProperty("The time when the ticket was bought")
        var timeOfPurchase: ZonedDateTime? = null,

        @ApiModelProperty("The id of the ticket")
        var id: String? = null
)