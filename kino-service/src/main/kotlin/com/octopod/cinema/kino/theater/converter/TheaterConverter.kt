package com.octopod.cinema.kino.show.converter

import com.octopod.cinema.kino.theater.dto.TheaterDto
import com.octopod.cinema.kino.theater.entity.Theater
import kotlin.streams.toList

class TheaterConverter {
    companion object {
        fun transform(theater: Theater): TheaterDto {
            return TheaterDto(
                    name = theater.name,
                    seatsMax = theater.seatsMax,
                    seatsEmpty = theater.seatsEmpty,
                    id = theater.id.toString()
            )
        }

        fun transform(theater: Iterable<Theater>): List<TheaterDto> {
            return theater.map { transform(it) }
        }

        fun transform(entities: List<Theater>, limit: Int): List<TheaterDto> {
            val dtoList : MutableList<TheaterDto> = entities.stream()
                    .limit(limit.toLong())
                    .map { transform(it) }
                    .toList().toMutableList()

            return dtoList
        }
    }
}