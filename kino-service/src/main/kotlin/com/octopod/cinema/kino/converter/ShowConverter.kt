package com.octopod.cinema.kino.converter

import com.octopod.cinema.common.hateos.HalPage
import com.octopod.cinema.kino.dto.ShowDto
import com.octopod.cinema.kino.entity.Show
import kotlin.streams.toList

class ShowConverter {
    companion object {
        fun transform(show: Show): ShowDto {
            return ShowDto(
                    startTime = show.startTime,
                    movieName = show.movieName,
                    cinemaId = show.cinemaId,
                    id = show.id
            )
        }

        fun transform(showdto: ShowDto): Show {
            return Show(
                    startTime = showdto.startTime,
                    movieName = showdto.movieName,
                    cinemaId = showdto.cinemaId,
                    id = showdto.id!!.toLong()
            )
        }

        fun transform(entities: List<Show>, page: Int, limit: Int): HalPage<ShowDto> {
            val offset = ((page - 1) * limit).toLong()
            val dtoList : MutableList<ShowDto> = entities.stream()
                    .skip(offset)
                    .limit(limit.toLong())
                    .map { transform(it) }
                    .toList().toMutableList()

            val pageDto = HalPage<ShowDto>()
            pageDto.data = dtoList
            pageDto.count = entities.size.toLong()
            pageDto.pages = ((pageDto.count / limit) + 1).toInt()

            return pageDto
        }
    }
}