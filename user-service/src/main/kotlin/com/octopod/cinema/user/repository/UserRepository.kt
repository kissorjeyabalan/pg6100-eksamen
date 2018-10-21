package com.octopod.cinema.user.repository

import com.octopod.cinema.user.entity.UserEntity
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntity, String>