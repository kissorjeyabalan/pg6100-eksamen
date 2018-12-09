package no.octopod.cinema.movie.repository

import no.octopod.cinema.movie.entity.MovieEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MovieRepository: CrudRepository<MovieEntity, Long> {
    fun findByFeaturedTrue(): List<MovieEntity>
}