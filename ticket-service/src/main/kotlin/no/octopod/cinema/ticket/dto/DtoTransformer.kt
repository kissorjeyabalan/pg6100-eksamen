package no.octopod.cinema.ticket.dto

import no.octopod.cinema.common.dto.TicketDto
import no.octopod.cinema.ticket.entity.Ticket
import kotlin.streams.toList
import no.octopod.cinema.common.hateos.HalPage

object DtoTransformer {

    fun transform(ticket: Ticket) : TicketDto {
        return TicketDto(ticket.userId,
                ticket.screeningId,
                ticket.timeOfPurchase,
                ticket.id.toString())
    }

    fun transform(ticketList: List<Ticket>,
                  page: Int,
                  limit: Int)
    : HalPage<TicketDto> {

        val offset = ((page - 1) * limit).toLong()

        val dtoList: MutableList<TicketDto> = ticketList.stream()
                .skip(offset)
                .limit(limit.toLong())
                .map { transform(it) }
                .toList().toMutableList()

        val pageDto = HalPage<TicketDto>()

        pageDto.data = dtoList
        pageDto.count = ticketList.size.toLong()
        pageDto.pages = ((pageDto.count / limit) + 1).toInt()

        return pageDto

    }
}