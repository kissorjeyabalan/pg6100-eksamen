package no.octopod.cinema.kino.repository

import no.octopod.cinema.kino.entity.ShowEntity
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.LockModeType

@Repository

interface ShowRepository: CrudRepository<ShowEntity, Long> {
    fun deleteAllById(ids: List<Long>)
    fun findAllByCinemaId(id: Long): List<ShowEntity>
    fun findAllByCinemaIdAndMovieId(cinemaId: Long, movieId: Long): List<ShowEntity>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun findById(id: Long): Optional<ShowEntity>
}