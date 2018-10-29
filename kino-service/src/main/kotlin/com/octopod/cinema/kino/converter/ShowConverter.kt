package com.octopod.cinema.kino.converter

import com.octopod.cinema.kino.dto.ShowDto
import com.octopod.cinema.kino.entity.Show
import kotlin.streams.toList

class ShowConverter {
    companion object {
        fun transform(show: Show): ShowDto {
            return ShowDto(
                    startTime = show.startTime,
                    movieName = show.movieName,
                    cinemaName = show.cinemaName,
                    id = show.id.toString()
            )
        }

        fun transform(showdto: ShowDto): Show {
            return Show(
                    startTime = showdto.startTime,
                    movieName = showdto.movieName,
                    cinemaName = showdto.cinemaName,
                    id = showdto.id!!.toLong()
            )
        }

        fun transform(entities: List<Show>, limit: Int): List<ShowDto> {
            val dtoList : MutableList<ShowDto> = entities.stream()
                    .limit(limit.toLong())
                    .map { transform(it) }
                    .toList().toMutableList()

            return dtoList
        }
    }
}