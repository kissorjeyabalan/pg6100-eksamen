package no.octopod.cinema.kino

import org.springframework.boot.runApplication

class LocalApplicationRunner: KinoApplication()

fun main(args: Array<String>) {
    runApplication<LocalApplicationRunner>(*args, "--spring.profiles.active=test")
}