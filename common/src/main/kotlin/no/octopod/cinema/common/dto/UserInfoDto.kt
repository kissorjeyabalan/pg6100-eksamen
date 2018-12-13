package no.octopod.cinema.common.dto

import io.swagger.annotations.ApiModelProperty
import java.time.ZonedDateTime

data class UserInfoDto(
    @ApiModelProperty("Phone number of user")
    var phone: String? = null,

    @ApiModelProperty("Email address of user")
    var email: String? = null,

    @ApiModelProperty("Name of user")
    var name: String? = null,

    @ApiModelProperty("When the user was created")
    var created: ZonedDateTime? = null,

    @ApiModelProperty("When the user was last updated")
    var updated: ZonedDateTime? = null
)