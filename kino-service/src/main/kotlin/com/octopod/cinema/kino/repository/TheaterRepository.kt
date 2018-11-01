package com.octopod.cinema.kino.repository

import com.octopod.cinema.kino.entity.Theater
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.Id
import javax.transaction.Transactional

@Repository
interface TheaterRepository: CrudRepository<Theater, Long>, TheaterRepositoryCustom {
    fun deleteAllById(ids: List<Long>)
}
@Transactional
interface TheaterRepositoryCustom {
    fun createTheater(name: String, seatsMax: Int): Long
}

@Repository
@Transactional
class TheaterRepositoryImpl: TheaterRepositoryCustom {
    @Autowired
    private lateinit var em: EntityManager

    override fun createTheater(name: String, seatsMax: Int): Long {
        val theater = Theater(name, seatsMax)

        em.persist(theater)
        return theater.id!!
    }
}