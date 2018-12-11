package no.octopod.cinema.ticket.repository

import no.octopod.cinema.ticket.entity.Ticket
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import javax.persistence.EntityManager

@Repository
interface TicketRepository : CrudRepository<Ticket, Long>, TicketRepositoryCustom {

    fun findAllByScreeningIdAndUserId(screeningId: String, userId: String): List<Ticket>

    fun findAllByUserId(userId: String): List<Ticket>

}

@Transactional
interface TicketRepositoryCustom {
    fun createTicket(userId: String, screeningId: String, seat: String): Long

    fun createTicket(userId: String, screeningId: String, timeOfPurchase: ZonedDateTime, seat: String): Long

    fun updateTicket(ticketId: Long, userId: String, screeningId: String, timeOfPurchase: ZonedDateTime, seat: String) : Boolean

}

@Repository
@Transactional
class TicketRepositoryImpl : TicketRepositoryCustom {

    @Autowired
    private lateinit var em: EntityManager

    override fun createTicket(userId: String, screeningId: String, seat: String): Long {
        val entity = Ticket(userId, screeningId, ZonedDateTime.now().withNano(0), seat)
        em.persist(entity)
        return entity.id!!
    }

    override fun createTicket(userId: String, screeningId: String, timeOfPurchase: ZonedDateTime, seat: String): Long {
        val entity = Ticket(userId, screeningId, timeOfPurchase, seat)
        em.persist(entity)
        return entity.id!!
    }

    override fun updateTicket(ticketId: Long,
                        userId: String,
                        screeningId: String,
                        timeOfPurchase: ZonedDateTime,
                        seat: String): Boolean {
        val ticket = em.find(Ticket::class.java, ticketId) ?: return false
        ticket.userId = userId
        ticket.screeningId = screeningId
        ticket.timeOfPurchase = timeOfPurchase
        ticket.seat = seat
        return true
    }



}