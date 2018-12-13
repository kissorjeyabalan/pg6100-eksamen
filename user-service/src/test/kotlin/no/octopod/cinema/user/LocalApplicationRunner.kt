package no.octopod.cinema.user

import org.springframework.boot.runApplication

class LocalApplicationRunner: UserApplication()

fun main(args: Array<String>) {
    runApplication<LocalApplicationRunner>(*args, "--spring.profiles.active=test")
}