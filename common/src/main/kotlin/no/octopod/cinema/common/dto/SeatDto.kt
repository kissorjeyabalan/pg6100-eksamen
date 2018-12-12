package no.octopod.cinema.common.dto

import io.swagger.annotations.ApiModelProperty

class SeatDto (
    @ApiModelProperty(value = "Seat in theatre")
    var seat: String? = null,

    @ApiModelProperty("Screening the seat belongs to")
    var screening_id: String? = null
)