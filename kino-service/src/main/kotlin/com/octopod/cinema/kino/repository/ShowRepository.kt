package com.octopod.cinema.kino.repository

import com.octopod.cinema.kino.entity.Show
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.Id
import javax.transaction.Transactional

/**
 * Some functions may not be in use, but might be used in further development
 */

@Repository
interface ShowRepository: CrudRepository<Show, Long>, ShowRepositoryCustom {
    fun deleteAllById(ids: List<Long>)
    fun findAllByCinemaId(id: Long): List<Show>
    fun findAllByCinemaIdAndMovieId(cinemaId: Long, movieId: Long): List<Show>
}

@Transactional
interface ShowRepositoryCustom {
    fun createShow(startTime: Int, movieName: Long, cinemaId: Long): Long
}

@Repository
@Transactional
class ShowRepositoryImpl: ShowRepositoryCustom {

    @Autowired
    private lateinit var em: EntityManager

    override fun createShow(startTime: Int, movieName: Long, cinemaId: Long): Long {
        val show = Show(startTime, movieName, cinemaId)

        em.persist(show)
        return show.id!!
    }
}