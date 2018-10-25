package com.octopod.cinema.kino.service

import com.octopod.cinema.kino.entity.Show
import com.octopod.cinema.kino.entity.Theater
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.TypedQuery
import javax.transaction.Transactional

@Service
@Transactional
class TheaterService {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun getTheater(id: Long): Theater {

        val theater = em.find(Theater::class.java, id)

        return theater
    }

    fun getTheaters(limit: Int): List<Theater> {

        val query: TypedQuery<Theater> = em.createQuery("SELECT t FROM Theater t", Theater::class.java)
        query.maxResults = limit
        val theaters = query.resultList

        return theaters
    }

    fun createTheater(name: String, seatsMax: Int, seatsEmpty: Int): Long? {
        val theater = Theater(name, seatsMax, seatsEmpty)

        em.persist(theater)
        return theater.id
    }
}