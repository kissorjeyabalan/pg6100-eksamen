package no.octopod.cinema.common.dto

import io.swagger.annotations.ApiModelProperty

class SendOrderDto(
    @ApiModelProperty("The screening ID the seat were bought for")
    var screening_id: String? = null,

    @ApiModelProperty("The payment token to validate the purchase against")
    var payment_token: String? = null,

    @ApiModelProperty("A list of seats that have been reserved and bought")
    var seats: List<String>? = null
)