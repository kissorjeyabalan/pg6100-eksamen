package no.octopod.cinema.booking.entity

import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class SeatReservationEntity(
        @get:Id @get:GeneratedValue
        var id: Long? = null,

        @get:NotBlank
        var seat: String? = null,

        @get:NotNull
        var reservationEndTime: ZonedDateTime? = null,

        @get:NotBlank
        var userId: String? = null,

        @get:NotNull
        var screeningId: Long? = null
)