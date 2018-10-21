package com.octopod.cinema.ticket.service

import com.octopod.cinema.ticket.entity.Ticket
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.TypedQuery

@Service
@Transactional
class TicketService {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun getTicket(id: Long): Ticket? {

        val ticket = em.find(Ticket::class.java, id)

        //if there will be a list fiel in ticket then load here

        return ticket
    }


    fun getTickets(limit: Int) : List<Ticket> {
        val query: TypedQuery<Ticket> = em.createQuery("select t from Ticket t", Ticket::class.java)
        query.maxResults = limit
        val result = query.resultList

        return result
    }

    fun getTicketsByScreeningIdAndUserId(limit: Int, userId: String, screeningId: String) : List<Ticket> {
        val query: TypedQuery<Ticket> =
                em.createQuery(
                        "select t from Ticket t where t.userId=?1 and t.screeningId=?2",
                        Ticket::class.java)

        query.setParameter(1, userId)
        query.setParameter(2, screeningId)

        query.maxResults = limit
        val result = query.resultList

        return result
    }

    fun getTicketsByUserId(limit: Int, userId: String) : List<Ticket> {
        val query: TypedQuery<Ticket> =
                em.createQuery(
                        "select t from Ticket t where t.userId=?1",
                        Ticket::class.java)

        query.setParameter(1, userId)

        query.maxResults = limit
        val result = query.resultList

        return result
    }

    fun createTicket(buyer: String, movieName: String, screeningId: String, movieStartTime: ZonedDateTime): Long? {
        val ticket = Ticket(buyer, movieName, screeningId,  ZonedDateTime.now(), movieStartTime)

        em.persist(ticket)
        return ticket.id
    }
}