package com.octopod.cinema.ticket.repository

import com.octopod.cinema.ticket.entity.Ticket
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


    fun createTicket(userId: String, screeningId: String): Long

}

@Repository
@Transactional
class TicketRepositoryImpl : TicketRepositoryCustom {

    @Autowired
    private lateinit var em: EntityManager

    override fun createTicket(userId: String, screeningId: String): Long {
        val entity = Ticket(userId, screeningId, ZonedDateTime.now())
        em.persist(entity)
        return entity.id!!
    }



}