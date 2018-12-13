package no.octopod.cinema.common.dto

import io.swagger.annotations.ApiModelProperty
import java.time.ZonedDateTime

data class OrderDto(
        @ApiModelProperty(value = "Internal ID of order")
        var id: Long? = null,

        @ApiModelProperty(value = "Time the order was placed")
        var order_time: ZonedDateTime? = null,

        @ApiModelProperty(value = "User the order belongs to")
        var user_id: String? = null,

        @ApiModelProperty(value = "Total price the user paid")
        var price: Int? = null,

        @ApiModelProperty(value = "Screening ID the order belongs to")
        var screening_id: Long? = null,

        @ApiModelProperty(value = "Token that was used to process the payment")
        var payment_token: String? = null,

        @ApiModelProperty(value = "List of ticket IDs")
        var tickets: List<Long>? = null
)