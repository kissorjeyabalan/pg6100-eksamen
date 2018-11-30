package com.octopod.cinema.kino.repository

import com.octopod.cinema.kino.entity.Theater
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
interface TheaterRepository: CrudRepository<Theater, Long> {
    fun deleteAllById(ids: List<Long>)
}