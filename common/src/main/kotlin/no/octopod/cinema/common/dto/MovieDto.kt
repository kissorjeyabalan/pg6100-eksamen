package no.octopod.cinema.common.dto

import io.swagger.annotations.ApiModelProperty
import java.time.ZonedDateTime

data class MovieDto(
        @ApiModelProperty(value = "Internal ID within the API for the movie")
        var id: String? = null,

        @ApiModelProperty(value = "Title of movie")
        var title: String? = null,

        @ApiModelProperty(value = "Description of movie")
        var description: String? = null,

        @ApiModelProperty(value = "Image path to poster of movie")
        var image_path: String? = null,

        @ApiModelProperty(value = "Movie premiere date")
        var release_date: ZonedDateTime? = null,

        @ApiModelProperty(value = "Movie is featured")
        var featured: Boolean = false
)