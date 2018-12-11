package no.octopod.cinema.booking.entity

import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class OrderEntity(
        @Id @GeneratedValue
        var id: Long? = null,
        var orderTime: ZonedDateTime? = null,
        var userId: String? = null,
        var price: Int? = null,
        var screeningId: Long? = null,
        var paymentToken: String? = null,
        var tickets: List<Long>? = null
)