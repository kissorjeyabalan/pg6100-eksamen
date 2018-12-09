package no.octopod.cinema.kino.converter

import no.octopod.cinema.common.hateos.HalPage
import no.octopod.cinema.kino.dto.TheaterDto
import no.octopod.cinema.kino.entity.TheaterEntity
import kotlin.streams.toList

class TheaterConverter {
    companion object {

        fun transform(theaterEntity: TheaterEntity): TheaterDto {

            return TheaterDto(
                    name = theaterEntity.name,
                    seatsMax = theaterEntity.seatsMax,
                    id = theaterEntity.id,
                    seats = theaterEntity.seats
            )
        }

        fun transform(theaterDto: TheaterDto): TheaterEntity {

            return TheaterEntity(
                    name = theaterDto.name,
                    seatsMax = theaterDto.seatsMax,
                    id = theaterDto.id!!.toLong(),
                    seats = theaterDto.seats
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