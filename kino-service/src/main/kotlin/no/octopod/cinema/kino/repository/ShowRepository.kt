package no.octopod.cinema.kino.repository

import no.octopod.cinema.kino.entity.ShowEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ShowRepository: CrudRepository<ShowEntity, Long> {
    fun deleteAllById(ids: List<Long>)
    fun findAllByCinemaId(id: Long): List<ShowEntity>
    fun findAllByCinemaIdAndMovieId(cinemaId: Long, movieId: Long): List<ShowEntity>
}