package com.octopod.cinema.kino.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
class Show (

        //TODO: Ta en vurdering på om dette burde være en annen type variabel
        @get:NotBlank
        var startTime: Int?,

        @get:NotBlank
        var movieName: String?,

        @get:NotBlank
        var cinemaName: String?,

        @get:Id @get:GeneratedValue
        var id: Long? = null

)