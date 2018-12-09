package no.octopod.cinema.booking.repository

import no.octopod.cinema.booking.entity.SeatReservationEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SeatReservationRepository: CrudRepository<SeatReservationEntity, Long> {
    fun countByUserIdAndScreeningId(userId: String, screeningId: Long): Long
    fun existsBySeatAndScreeningId(seat: String, screeningId: Long): Boolean
    fun findAllByUserIdAndScreeningId(userId: String, screeningId: Long): List<SeatReservationEntity>
    fun deleteAllByUserIdAndScreeningId(userId: String, screeningId: Long): Void
    fun deleteBySeatAndScreeningId(seat: String, screeningId: Long)
}