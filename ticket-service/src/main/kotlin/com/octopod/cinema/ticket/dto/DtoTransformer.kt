package com.octopod.cinema.ticket.dto

import com.octopod.cinema.ticket.entity.Ticket
import com.octopod.cinema.ticket.hal.PageDto
import kotlin.streams.toList

object DtoTransformer {

    fun transform(ticket: Ticket) : TicketDto {
        return TicketDto(ticket.userId,
                         ticket.movieName,
                         ticket.screeningId,
                         ticket.timeOfPurchase,
                         ticket.movieStartTime,
                         ticket.id.toString())
    }

    fun transform(ticketList: List<Ticket>,
                  offset: Int,
                  limit: Int)
    : PageDto<TicketDto> {

        val dtoList: MutableList<TicketDto> = ticketList.stream()
                .skip(offset.toLong())
                .limit(limit.toLong())
                .map { transform(it)}
                .toList().toMutableList()

        return PageDto(
                list = dtoList,
                rangeMin = offset,
                rangeMax = offset + dtoList.size - 1,
                totalSize = ticketList.size
        )

    }
}