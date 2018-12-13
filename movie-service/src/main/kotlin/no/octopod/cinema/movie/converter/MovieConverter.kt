package no.octopod.cinema.movie.converter

import no.octopod.cinema.common.dto.MovieDto
import no.octopod.cinema.common.hateos.HalPage
import no.octopod.cinema.movie.entity.MovieEntity
import kotlin.streams.toList

class MovieConverter {
    companion object {

        fun transform(movie: MovieEntity): MovieDto {
            return MovieDto(
                    id = movie.id?.toString(),
                    title = movie.title,
                    description = movie.description,
                    image_path = movie.imagePath,
                    release_date = movie.releaseDate?.withFixedOffsetZone()?.withNano(0),
                    featured = movie.featured
            )
        }

        fun transform(movieDto: MovieDto): MovieEntity {
            return MovieEntity(
                id = movieDto.id?.toLongOrNull(),
                title = movieDto.title,
                description = movieDto.description,
                imagePath = movieDto.image_path,
                releaseDate = movieDto.release_date?.withFixedOffsetZone()?.withNano(0),
                featured = movieDto.featured
            )
        }

        fun transform(entities: List<MovieEntity>, page: Int, limit: Int): HalPage<MovieDto> {
            val offset = ((page - 1) * limit).toLong()
            val dtoList : MutableList<MovieDto> = entities.stream()
                    .skip(offset)
                    .limit(limit.toLong())
                    .map { transform(it) }
                    .toList().toMutableList()

            val pageDto = HalPage<MovieDto>()
            pageDto.data = dtoList
            pageDto.count = entities.size.toLong()
            pageDto.pages = ((pageDto.count / limit)).toInt()
            if (pageDto.pages == 0) pageDto.pages += 1

            return pageDto
        }
    }
}