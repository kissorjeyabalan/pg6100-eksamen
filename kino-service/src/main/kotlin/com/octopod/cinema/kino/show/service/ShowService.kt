package com.octopod.cinema.kino.show.service

import com.octopod.cinema.kino.show.entity.Show
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.TypedQuery
import javax.transaction.Transactional

@Service
@Transactional
class ShowService {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun getShow(id: Long): Show {

        val show = em.find(Show::class.java, id)

        return show
    }

    fun getShows(limit: Int): List<Show> {

        val query: TypedQuery<Show> = em.createQuery("SELECT s FROM Show s", Show::class.java)
        query.maxResults = limit
        val shows = query.resultList

        return shows
    }

    //TODO: Vurder om denne trengs eller ikke
    fun getShowsByTheater(limit: Int, theater: String): List<Show> {

        val query: TypedQuery<Show> = em.createQuery(
                "SELECT s FROM Show s WHERE s.cinema=?1",
                Show::class.java
        )

        query.setParameter(1, theater)
        query.maxResults = limit
        val shows = query.resultList

        return shows
    }

    fun createShow(startTime: Int, movieName: String, cinemaName: String): Long? {

        val show = Show(startTime, movieName, cinemaName)

        em.persist(show)
        return show.id
    }
}