package com.octopod.cinema.kino.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
class Theater (

        @get:NotBlank
        var name: String,

        @get:NotBlank
        var seatsMax: Int,

        @get:NotBlank
        var seatsEmpty: Int,

        @get:Id @get:GeneratedValue
        var id: Long? = null

)