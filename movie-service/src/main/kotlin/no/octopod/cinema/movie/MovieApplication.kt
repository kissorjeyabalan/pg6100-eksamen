package no.octopod.cinema.movie

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class MovieApplication

fun main(args: Array<String>) {
    runApplication<MovieApplication>(*args)
}