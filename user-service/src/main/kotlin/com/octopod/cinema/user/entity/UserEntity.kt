package com.octopod.cinema.user.entity

import com.octopod.cinema.common.dto.TicketDto
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.PrePersist
import javax.persistence.PreUpdate
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
data class UserEntity
(       @Id
        @get:NotBlank @get:Size(max = 50)
        var phone: String? = null,

        @get:NotBlank @get:Size(max = 120)
        var email: String? = null,

        @get:NotBlank @get:Size(max = 128)
        var name: String? = null,

        @get:NotNull
        var creationTime: ZonedDateTime,

        @get:NotNull
        var updatedTime: ZonedDateTime
) {
    @PrePersist
    fun onCreate() {
        creationTime = ZonedDateTime.now()
        updatedTime = ZonedDateTime.now()
    }

    @PreUpdate
    fun onUpdate() {
        updatedTime = ZonedDateTime.now()
    }
}