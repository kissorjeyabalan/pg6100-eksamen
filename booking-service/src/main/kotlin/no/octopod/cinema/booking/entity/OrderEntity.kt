package no.octopod.cinema.booking.entity

import java.time.ZonedDateTime

class OrderEntity(
        var id: Long? = null,
        var orderTime: ZonedDateTime? = null,
        var userId: String? = null,
        var price: Int? = null,
        var screeningId: Long? = null,
        var paymentToken: String? = null
)