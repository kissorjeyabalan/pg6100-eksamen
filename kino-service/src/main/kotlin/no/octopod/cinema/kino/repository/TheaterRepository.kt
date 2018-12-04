package no.octopod.cinema.kino.repository

import no.octopod.cinema.kino.entity.TheaterEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Some functions may not be in use, but might be used in further development
 */

@Repository
interface TheaterRepository: CrudRepository<TheaterEntity, Long> {
    fun deleteAllById(ids: List<Long>)
}