package dto

import java.time.ZonedDateTime

data class UserDto(
    var phone: String? = null,
    var email: String? = null,
    var created: ZonedDateTime? = null,
    var updated: ZonedDateTime? = null
)