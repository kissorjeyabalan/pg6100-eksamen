package com.octopod.cinema.kino.converter

import com.octopod.cinema.common.hateos.HalPage
import com.octopod.cinema.kino.dto.TheaterDto
import com.octopod.cinema.kino.entity.Theater
import kotlin.streams.toList

class TheaterConverter {
    companion object {

        fun transform(theater: Theater): TheaterDto {
            return TheaterDto(
                    name = theater.name,
                    seatsMax = theater.seatsMax,
                    id = theater.id
            )
        }

        fun transform(theaterdto: TheaterDto): Theater {
            return Theater(
                    name = theaterdto.name,
                    seatsMax = theaterdto.seatsMax,
                    id = theaterdto.id!!.toLong()
            )
        }

        fun transform(entities: List<Theater>, page: Int, limit: Int): HalPage<TheaterDto> {
            val offset = ((page - 1) * limit).toLong()
            val dtoList : MutableList<TheaterDto> = entities.stream()
                    .skip(offset)
                    .limit(limit.toLong())
                    .map { transform(it) }
                    .toList().toMutableList()

            val pageDto = HalPage<TheaterDto>()
            pageDto.data = dtoList
            pageDto.count = entities.size.toLong()
            pageDto.pages = ((pageDto.count / limit) + 1).toInt()

            return pageDto
        }
    }
}