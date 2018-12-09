package no.octopod.cinema.kino.converter

import no.octopod.cinema.common.hateos.HalPage
import no.octopod.cinema.kino.dto.ShowDto
import no.octopod.cinema.kino.entity.ShowEntity
import kotlin.streams.toList

class ShowConverter {
    companion object {

        fun transform(showEntity: ShowEntity): ShowDto {

            return ShowDto(
                    startTime = showEntity.startTime,
                    movieId = showEntity.movieId!!,
                    cinemaId = showEntity.cinemaId!!,
                    availableSeats = showEntity.seats,
                    id = showEntity.id
            )
        }

        fun transform(showDto: ShowDto): ShowEntity {

            return ShowEntity(
                    startTime = showDto.startTime,
                    movieId = showDto.movieId!!,
                    cinemaId = showDto.cinemaId!!,
                    seats = showDto.availableSeats,
                    id = showDto.id!!.toLong()
            )
        }

        fun transform(entities: List<ShowEntity>, page: Int, limit: Int): HalPage<ShowDto> {

            val offset = ((page - 1) * limit).toLong()
            val dtoList : MutableList<ShowDto> = entities.stream()
                    .skip(offset)
                    .limit(limit.toLong())
                    .map { transform(it) }
                    .toList().toMutableList()

            val pageDto = HalPage<ShowDto>()
            pageDto.data = dtoList
            pageDto.count = entities.size.toLong()
            pageDto.pages = ((pageDto.count / limit)).toInt()

            return pageDto
        }
    }
}