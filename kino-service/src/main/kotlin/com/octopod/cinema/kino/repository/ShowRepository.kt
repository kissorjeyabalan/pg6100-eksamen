package com.octopod.cinema.kino.repository

import com.octopod.cinema.kino.entity.Show
import com.octopod.cinema.kino.entity.Theater
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Repository
interface ShowRepository : CrudRepository<Show, Long>, ShowRepositoryCustom {
    fun deleteAllById(ids: List<Long>)
}
@Transactional
interface ShowRepositoryCustom {
    fun createShow(startTime: Int, movieName: String, cinemaName: String): Long
}

@Repository
@Transactional
class ShowRepositoryImpl : ShowRepositoryCustom {
    @Autowired
    private lateinit var em: EntityManager

    override fun createShow(startTime: Int, movieName: String, cinemaName: String): Long {
        val show = Show(startTime, movieName, cinemaName)

        em.persist(show)
        return show.id!!
    }
}