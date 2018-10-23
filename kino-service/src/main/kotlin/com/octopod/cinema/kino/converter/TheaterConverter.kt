package com.octopod.cinema.kino.converter

import com.octopod.cinema.kino.dto.TheaterDto
import com.octopod.cinema.kino.entity.Theater
import com.octopod.cinema.kino.service.TheaterService
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

        fun transform(entities : List<Theater>, limit : Int) : List<TheaterDto> {
            val dtoList : MutableList<TheaterDto> = entities.stream()
                    .limit(limit.toLong())
                    .map { transform(it) }
                    .toList().toMutableList()

            return dtoList
        }
    }
}