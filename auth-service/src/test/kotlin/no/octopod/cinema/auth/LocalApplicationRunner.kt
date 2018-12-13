package no.octopod.cinema.auth

import org.springframework.boot.runApplication


class LocalApplicationRunner: AuthApplication()

    fun main(args: Array<String>) {
    runApplication<LocalApplicationRunner>(*args, "--spring.profiles.active=localrunner")
}