package com.octopod.cinema.eureka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.codec.ServerCodecConfigurer

@EnableEurekaServer
@SpringBootApplication
class EurekaApplication {
    @Bean
    fun serverCodecConfigurer(): ServerCodecConfigurer {
        return ServerCodecConfigurer.create()
    }
}

fun main(args: Array<String>) {
    runApplication<EurekaApplication>(*args)
}
