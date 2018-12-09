package no.octopod.cinema.booking.controller

import no.octopod.cinema.booking.dto.SeatDto
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping(
        path = ["/booking"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
class BookingController {
    private val MAX_RESERVATIONS_PER_USER = 5

/*    @PostMapping(path = ["/reserve"])
    fun toggleSeatReservation(
            @RequestBody seatDto: SeatDto
    ): ResponseEntity<Void> {
        if (seatDto.seat.isNullOrEmpty()) {
            return ResponseEntity.status(400).build()
        }

        //val current =

        /**
         * TODO:
         * Reserving seat by adding,
         * Purge reservations: https://www.callicoder.com/spring-boot-quartz-scheduler-email-scheduling-example/
         */

    }*/
}