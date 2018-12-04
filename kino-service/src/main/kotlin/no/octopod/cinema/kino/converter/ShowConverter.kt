package no.octopod.cinema.kino.converter

import no.octopod.cinema.common.hateos.HalPage
import no.octopod.cinema.kino.dto.ShowDto
import no.octopod.cinema.kino.entity.ShowEntity
import kotlin.streams.toList

class ShowConverter {
    companion object {

        fun transform(show: ShowEntity): ShowDto {
            return ShowDto(
                    startTime = show.startTime,
                    movieId = show.movieId!!.toString(),
                    cinemaId = show.cinemaId!!.toString(),
                    id = show.id
            )
        }

        fun transform(showdto: ShowDto): ShowEntity {
            return ShowEntity(
                    startTime = showdto.startTime,
                    movieId = showdto.movieId!!.toLong(),
                    cinemaId = showdto.cinemaId!!.toLong(),
                    id = showdto.id!!.toLong()
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