package no.octopod.cinema.booking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.netflix.ribbon.RibbonClient
import org.springframework.cloud.netflix.ribbon.RibbonClients
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@SpringBootApplication
@EnableEurekaClient
@RibbonClients(value = [(RibbonClient(name = "ticket-service")), (RibbonClient(name = "kino-service"))])
class BookingApplication {
    @LoadBalanced
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}

fun main(args: Array<String>) {
    runApplication<BookingApplication>(*args)
}