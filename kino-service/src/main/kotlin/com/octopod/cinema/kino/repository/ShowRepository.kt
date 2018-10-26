package com.octopod.cinema.kino.repository

import com.octopod.cinema.kino.entity.Show
import org.springframework.data.repository.CrudRepository

interface ShowRepository : CrudRepository<Show, String>