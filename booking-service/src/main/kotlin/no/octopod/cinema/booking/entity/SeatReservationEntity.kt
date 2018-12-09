package no.octopod.cinema.booking.entity

import java.time.ZonedDateTime

class SeatReservationEntity(
        var id: Long? = null,
        var seat: String? = null,
        var reservationEndTime: ZonedDateTime? = null,
        var userId: String? = null,
        var screeningId: Long? = null
)