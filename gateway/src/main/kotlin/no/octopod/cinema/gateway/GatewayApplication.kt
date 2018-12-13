package no.octopod.cinema.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.web.cors.CorsConfiguration
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping
import org.springframework.http.HttpMethod
import java.util.*


@EnableDiscoveryClient
@SpringBootApplication
class GatewayApplication {
    @Bean
    fun corsConfiguration(routePredicateHandlerMapping: RoutePredicateHandlerMapping): CorsConfiguration {
        val corsConfiguration = CorsConfiguration().applyPermitDefaultValues()
        Arrays.asList(HttpMethod.OPTIONS, HttpMethod.PUT, HttpMethod.GET, HttpMethod.DELETE, HttpMethod.POST).forEach {
            m -> corsConfiguration.addAllowedMethod(m) }
        corsConfiguration.addAllowedOrigin("http://localhost:8080")
        corsConfiguration.addAllowedOrigin("http://localhost:80")
        corsConfiguration.addAllowedOrigin("http://127.0.0.1:80")
        corsConfiguration.addAllowedOrigin("http://127.0.0.1:8080")
        corsConfiguration.addAllowedOrigin("*")
        corsConfiguration.allowCredentials = true
        corsConfiguration.addAllowedHeader("*")
        routePredicateHandlerMapping.setCorsConfigurations(object : HashMap<String, CorsConfiguration>() {
            init {
                put("/**", corsConfiguration)
            }
        })
        return corsConfiguration
    }
}

fun main(args: Array<String>) {
    runApplication<GatewayApplication>(*args)
}