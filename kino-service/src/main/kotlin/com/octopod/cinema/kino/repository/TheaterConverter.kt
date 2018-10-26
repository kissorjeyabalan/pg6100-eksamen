package com.octopod.cinema.kino.repository

import com.octopod.cinema.kino.entity.Theater
import org.springframework.data.repository.CrudRepository

interface TheaterRepository : CrudRepository<Theater, String>