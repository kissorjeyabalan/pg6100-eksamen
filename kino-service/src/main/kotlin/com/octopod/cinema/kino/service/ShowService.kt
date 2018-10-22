package com.octopod.cinema.kino.service

import com.octopod.cinema.kino.entity.Show
import com.octopod.cinema.kino.entity.Theater
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Service
@Transactional
class ShowService {

    @PersistenceContext
    private lateinit var em : EntityManager

    fun getShow(id: Long) : Show? {
        return show
    }

    fun getShows(limit : Int, theater : Theater) : List<Show> {
        return shows
    }

    fun createShow(startTime : Int, movie : String) : Long? {
        val show = Show(startTime, movie)

        em.persist(show)
        return show.id
    }
}