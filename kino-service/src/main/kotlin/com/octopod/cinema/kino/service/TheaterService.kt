package com.octopod.cinema.kino.service

import com.octopod.cinema.kino.entity.Show
import com.octopod.cinema.kino.entity.Theater
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Service
@Transactional
class TheaterService {

    @PersistenceContext
    private lateinit var em : EntityManager

    fun getTheater(id: Long) : Theater? {
        return theater
    }

    fun getTheaters(limit : Int) : List<Theater> {
        return theaters
    }

    fun getSeatsMax(theater : Theater) : Int? {
        return seatsMax
    }

    fun getSeatsEmpty(theater : Theater) : Int? {
        return seatsEmpty
    }

    fun getShows(theater : Theater) : List<Show> {
        return shows
    }

    fun createTheater(name : String, seatsMax : Int, seatsEmpty : Int) : Long? {
        val theater = Theater(name, seatsMax, seatsEmpty)

        em.persist(theater)
        return theater.id
    }
}