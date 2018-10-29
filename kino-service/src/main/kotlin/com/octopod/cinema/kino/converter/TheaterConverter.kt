package com.octopod.cinema.kino.converter

import com.octopod.cinema.kino.dto.TheaterDto
import com.octopod.cinema.kino.entity.Theater
import kotlin.streams.toList

class TheaterConverter {
    companion object {
        fun transform(theater: Theater): TheaterDto {
            return TheaterDto(
                    name = theater.name,
                    seatsMax = theater.seatsMax,
                    id = theater.id.toString()
            )
        }

        fun transform(theaterdto: TheaterDto): Theater {
            return Theater(
                    name = theaterdto.name,
                    seatsMax = theaterdto.seatsMax,
                    id = theaterdto.id!!.toLong()
            )
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