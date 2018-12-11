package no.octopod.cinema.booking.controller

import no.octopod.cinema.booking.dto.SeatDto
import no.octopod.cinema.booking.entity.SeatReservationEntity
import no.octopod.cinema.booking.repository.OrderRepository
import no.octopod.cinema.booking.repository.SeatReservationRepository
import no.octopod.cinema.common.utility.SecurityUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.lang.Exception
import java.time.ZonedDateTime
import java.util.*

@RequestMapping(
        path = ["/order"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class BookingController {
    private val MAX_RESERVATIONS_PER_USER = 5

    @Autowired private lateinit var seatReserverationRepo: SeatReservationRepository
    @Autowired private lateinit var orderRepo: OrderRepository

    @Value("\${kinoApiAddress}") private lateinit var kinoApiAddress: String
    @Value("\${ticketApiAddress") private lateinit var ticketApiAddress: String
    @Value("\${systemUser}") private lateinit var systemUser: String
    @Value("\${systemPwd}") private lateinit var systemPwd: String


    private val client: RestTemplate = RestTemplate()

    @PostMapping(path = ["/reserve"])
    fun toggleSeatReservation(
            @RequestBody seatDto: SeatDto,
            authentication: Authentication
    ): ResponseEntity<Void> {
        if (seatDto.seat.isNullOrEmpty() || seatDto.screening_id?.toLongOrNull() == null) {
            return ResponseEntity.status(400).build()
        }

        val userId = SecurityUtil.getUserId(authentication)

        val reservedSeat = seatReserverationRepo.findBySeatAndScreeningId(seatDto.seat!!, seatDto.screening_id!!.toLong()).orElse(null)
        if (reservedSeat == null) {
            val currentUserReservations =
                    seatReserverationRepo.countByUserIdAndScreeningId(userId, seatDto.screening_id!!.toLong())

            if (currentUserReservations >= MAX_RESERVATIONS_PER_USER) {
                return ResponseEntity.status(403).build()
            }

            // remove from seats
            try {
                client.exchange(
                        "$kinoApiAddress/shows/${seatDto.screening_id}/seats/${seatDto.seat}",
                        HttpMethod.DELETE,
                        getSystemAuthorizationHeader(),
                        Any::class.java
                )
                seatReserverationRepo.save(
                        SeatReservationEntity(
                                seat = seatDto.seat,
                                userId = userId,
                                reservationEndTime = ZonedDateTime.now().plusMinutes(5),
                                screeningId = seatDto.screening_id!!.toLong()
                        )
                )
            } catch (e: Exception) {
                return ResponseEntity.status(500).build()
            }

            return ResponseEntity.status(204).build()
        } else if (reservedSeat.userId == userId) {
            // add back to seats
            try {
                client.exchange(
                        "$kinoApiAddress/shows/${seatDto.screening_id}/seats/${seatDto.seat}",
                        HttpMethod.POST,
                        getSystemAuthorizationHeader(),
                        Any::class.java
                )
                seatReserverationRepo.delete(reservedSeat)
            } catch (e: Exception) {
                return ResponseEntity.status(500).build()
            }
        } else if (reservedSeat.userId != null && reservedSeat.userId != userId) {
            return ResponseEntity.status(409).build()
        }

        /**
         * TODO:
         * Reserving seat by adding,
         * Purge reservations: https://www.callicoder.com/spring-boot-quartz-scheduler-email-scheduling-example/
         */
        return ResponseEntity.status(400).build()
    }

    private fun getAuthorizationHeader(authorization: String): HttpEntity<Any> {
        val headers = HttpHeaders()
        headers.add("Authorization", authorization)
        headers.add("User-Agent", "BookingService/User 0.0.1")
        return HttpEntity<Any>(null, headers)
    }

    private fun getSystemAuthorizationHeader(): HttpEntity<Any> {
        val headers = HttpHeaders()
        val auth = "$systemUser:$systemPwd"
        val authBytes = auth.toByteArray()
        val encodedAuth = Base64.getEncoder().encode(authBytes)
        val base64auth = String(encodedAuth)
        val authHeader = "Basic $base64auth"
        headers.add("Authorization", authHeader)
        headers.add("User-Agent", "BookingService/System 0.0.1")
        return HttpEntity<Any>(null, headers)
    }
}