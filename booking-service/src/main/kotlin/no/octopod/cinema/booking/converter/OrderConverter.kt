package no.octopod.cinema.booking.converter

import no.octopod.cinema.booking.dto.OrderDto
import no.octopod.cinema.booking.entity.OrderEntity
import no.octopod.cinema.common.hateos.HalPage
import kotlin.streams.toList

class OrderConverter {
    companion object {
        fun transform(orderEntity: OrderEntity): OrderDto {
            return OrderDto(
                    id = orderEntity.id,
                    order_time = orderEntity.orderTime,
                    user_id = orderEntity.userId,
                    price = orderEntity.price,
                    screening_id = orderEntity.screeningId,
                    payment_token = orderEntity.paymentToken,
                    tickets = orderEntity.tickets
            )
        }

        fun transform(entities: List<OrderEntity>, page: Int, limit: Int): HalPage<OrderDto> {
            val offset = ((page - 1) * limit).toLong()
            val dtoList: MutableList<OrderDto> = entities.stream()
                    .skip(offset)
                    .limit(limit.toLong())
                    .map { transform(it) }
                    .toList().toMutableList()

            val pageDto = HalPage<OrderDto>()
            pageDto.data = dtoList
            pageDto.count = entities.size.toLong()
            pageDto.pages = ((pageDto.count / limit)).toInt()

            return pageDto
        }
    }
}