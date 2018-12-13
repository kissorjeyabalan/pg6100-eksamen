package no.octopod.cinema.booking.entity

import java.time.ZonedDateTime
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class OrderEntity(
        @get:Id @get:GeneratedValue
        var id: Long? = null,

        @get:NotNull
        var orderTime: ZonedDateTime? = null,

        @get:NotBlank
        var userId: String? = null,

        @get:NotNull
        var price: Int? = null,

        @get:NotNull
        var screeningId: Long? = null,

        @get:NotBlank
        var paymentToken: String? = null,

        @get:NotNull
        @get:ElementCollection
        var tickets: List<Long>? = listOf()
)