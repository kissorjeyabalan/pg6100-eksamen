package no.octopod.cinema.ticket

import org.springframework.boot.runApplication

class LocalApplicationRunner: TicketServiceApplication()

fun main(args: Array<String>) {
    runApplication<LocalApplicationRunner>(*args, "--spring.profiles.active=test")
}