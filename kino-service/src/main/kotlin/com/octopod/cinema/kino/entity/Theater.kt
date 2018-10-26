package com.octopod.cinema.kino.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class Theater (

        @get:NotBlank
        var name: String? = null,

        @get:NotNull
        var seatsMax: Int? = null,

        @get:NotNull
        var seatsEmpty: Int? = null,

        @get:Id @get:GeneratedValue
        var id: Long? = null

)