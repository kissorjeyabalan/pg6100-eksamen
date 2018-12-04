package no.octopod.cinema.kino.repository

import no.octopod.cinema.kino.entity.ShowEntity
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
interface ShowRepository: CrudRepository<ShowEntity, Long> {
    fun deleteAllById(ids: List<Long>)
    fun findAllByCinemaId(id: Long): List<ShowEntity>
    fun findAllByCinemaIdAndMovieId(cinemaId: Long, movieId: Long): List<ShowEntity>
}