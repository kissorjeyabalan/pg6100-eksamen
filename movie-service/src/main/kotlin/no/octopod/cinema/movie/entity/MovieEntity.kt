package no.octopod.cinema.movie.entity

import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class MovieEntity(
    @get:Id @get:GeneratedValue
    var id: Long? = null,

    @get:NotEmpty @Size(min = 1, max = 125)
    var title: String? = null,

    @get:Size(min = 0, max = 500)
    var description: String? = null,

    var imagePath: String? = null,

    @get:NotNull
    var releaseDate: ZonedDateTime? = null,

    @get:NotNull
    var featured: Boolean = false
)