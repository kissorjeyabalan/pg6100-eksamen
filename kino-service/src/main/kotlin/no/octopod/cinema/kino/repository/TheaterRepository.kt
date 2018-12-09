package no.octopod.cinema.kino.repository

import no.octopod.cinema.kino.entity.TheaterEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TheaterRepository: CrudRepository<TheaterEntity, Long> {
    fun deleteAllById(ids: List<Long>)
}