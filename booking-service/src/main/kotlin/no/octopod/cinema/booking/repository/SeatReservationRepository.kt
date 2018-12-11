package no.octopod.cinema.booking.repository

import no.octopod.cinema.booking.entity.SeatReservationEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SeatReservationRepository: CrudRepository<SeatReservationEntity, Long> {
    fun countByUserIdAndScreeningId(userId: String, screeningId: Long): Long
    fun existsBySeatAndScreeningId(seat: String, screeningId: Long): Boolean
    fun existsByUserIdAndScreeningIdAndSeat(userId: String, screeningId: Long, seat: String): Boolean
    fun findAllByUserIdAndScreeningId(userId: String, screeningId: Long): List<SeatReservationEntity>
    fun findBySeatAndScreeningId(seat: String, screeningId: Long): Optional<SeatReservationEntity>
    fun deleteAllByUserIdAndScreeningId(userId: String, screeningId: Long): Void
    fun deleteBySeatAndScreeningId(seat: String, screeningId: Long)
}