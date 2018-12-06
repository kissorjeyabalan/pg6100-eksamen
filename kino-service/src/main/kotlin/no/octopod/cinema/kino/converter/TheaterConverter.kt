package no.octopod.cinema.kino.converter

import no.octopod.cinema.common.hateos.HalPage
import no.octopod.cinema.kino.dto.TheaterDto
import no.octopod.cinema.kino.entity.TheaterEntity
import kotlin.streams.toList

class TheaterConverter {
    companion object {

        fun transform(theater: TheaterEntity): TheaterDto {
            return TheaterDto(
                    name = theater.name,
                    seatsMax = theater.seatsMax,
                    id = theater.id,
                    seats = theater.seats
            )
        }

        fun transform(theaterdto: TheaterDto): TheaterEntity {
            return TheaterEntity(
                    name = theaterdto.name,
                    seatsMax = theaterdto.seatsMax,
                    id = theaterdto.id!!.toLong(),
                    seats = theaterdto.seats
            )
        }

        fun transform(entities: List<TheaterEntity>, page: Int, limit: Int): HalPage<TheaterDto> {
            val offset = ((page - 1) * limit).toLong()
            val dtoList : MutableList<TheaterDto> = entities.stream()
                    .skip(offset)
                    .limit(limit.toLong())
                    .map { transform(it) }
                    .toList().toMutableList()

            val pageDto = HalPage<TheaterDto>()
            pageDto.data = dtoList
            pageDto.count = entities.size.toLong()
            pageDto.pages = ((pageDto.count / limit + 1)).toInt()

            return pageDto
        }
    }
}