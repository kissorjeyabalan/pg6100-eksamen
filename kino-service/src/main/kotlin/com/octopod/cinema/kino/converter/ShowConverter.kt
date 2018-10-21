package com.octopod.cinema.kino.converter

import com.octopod.cinema.kino.dto.ShowDto
import com.octopod.cinema.kino.entity.Show

class ShowConverter {
    companion object {
        fun transform(show: Show): ShowDto {
            return ShowDto(
                    startTime = show.startTime,
                    movie = show.movie,
                    id = show.id.toString()
            )
        }

        fun transform(shows: Iterable<Show>): List<ShowDto> {
            return shows.map { transform(it) }
        }
    }
}