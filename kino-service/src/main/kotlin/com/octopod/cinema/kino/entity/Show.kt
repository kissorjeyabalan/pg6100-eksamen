package com.octopod.cinema.kino.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class Show (

        @get:NotNull
        var startTime: Int? = null,

        @get:NotBlank
        var movieName: String? = null,

        @get:NotBlank
        var cinemaId: String? = null,

        @get:Id @get:GeneratedValue
        var id: Long? = null

)