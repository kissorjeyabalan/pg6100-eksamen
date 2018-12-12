package no.octopod.cinema.common.dto

class SendOrderDto(
    var screening_id: String? = null,
    var payment_token: String? = null,
    var seats: List<String>? = null
)