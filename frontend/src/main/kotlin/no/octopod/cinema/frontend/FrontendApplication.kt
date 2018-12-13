package no.octopod.cinema.frontend
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter






@Controller
@SpringBootApplication
@EnableEurekaClient
class FrontendApplication {

    @GetMapping(value = "/")
    fun index(): String {
        return "index"
    }
}


fun main(args: Array<String>) {
    runApplication<FrontendApplication>(*args)
}

