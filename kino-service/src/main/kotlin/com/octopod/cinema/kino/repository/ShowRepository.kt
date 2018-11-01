package com.octopod.cinema.kino.repository

import com.octopod.cinema.kino.entity.Show
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Repository
interface ShowRepository: CrudRepository<Show, Long>, ShowRepositoryCustom {
    fun deleteAllById(ids: List<Long>)
}
@Transactional
interface ShowRepositoryCustom {
    fun createShow(show: Show): Long
}

@Repository
@Transactional
class ShowRepositoryImpl: ShowRepositoryCustom {
    @Autowired
    private lateinit var em: EntityManager

    override fun createShow(show: Show): Long {

        em.persist(show)
        return show.id!!
    }
}