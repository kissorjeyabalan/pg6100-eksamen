package no.octopod.cinema.movie

import org.springframework.boot.runApplication

class LocalApplicationRunner: MovieApplication()

fun main(args: Array<String>) {
    runApplication<LocalApplicationRunner>(*args, "--spring.profiles.active=local")
}