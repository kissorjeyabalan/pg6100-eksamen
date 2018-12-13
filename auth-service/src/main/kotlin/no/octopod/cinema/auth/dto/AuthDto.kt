package no.octopod.cinema.auth.dto

import io.swagger.annotations.ApiModelProperty

public data class AuthDto(
        @ApiModelProperty("Username")
        var username: String? = null,

        @ApiModelProperty("Password")
        var password: String? = null
)