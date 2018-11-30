package no.octopod.cinema.user.repository

import no.octopod.cinema.user.entity.UserEntity
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntity, String>