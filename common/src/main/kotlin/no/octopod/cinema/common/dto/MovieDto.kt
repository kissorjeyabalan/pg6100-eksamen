package no.octopod.cinema.common.dto

import java.time.ZonedDateTime

data class MovieDto(
        var id: String? = null,
        var title: String? = null,
        var description: String? = null,
        var image_path: String? = null,
        var release_date: ZonedDateTime? = null,
        var featured: Boolean = false
)