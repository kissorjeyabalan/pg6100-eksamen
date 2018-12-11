package no.octopod.cinema.booking.entity

import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class SeatReservationEntity(
        @Id @GeneratedValue
        var id: Long? = null,

        var seat: String? = null,
        var reservationEndTime: ZonedDateTime? = null,
        var userId: String? = null,
        var screeningId: Long? = null
)