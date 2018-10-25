package com.octopod.cinema.kino.show.converter

import com.octopod.cinema.kino.show.dto.ShowDto
import com.octopod.cinema.kino.show.entity.Show
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

        fun transform(shows: Iterable<Show>): List<ShowDto> {
            return shows.map { transform(it) }
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