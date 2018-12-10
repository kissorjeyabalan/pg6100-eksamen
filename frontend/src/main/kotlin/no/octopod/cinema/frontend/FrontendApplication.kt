package no.octopod.cinema.frontend
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping




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

