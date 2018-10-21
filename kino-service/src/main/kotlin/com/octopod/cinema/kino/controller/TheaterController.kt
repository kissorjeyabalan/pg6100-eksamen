package com.octopod.cinema.kino.controller

import io.swagger.annotations.Api
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(value = "theater", description = "Handling theaters")
@RequestMapping(
        path = ["/theater"]
)
@RestController
class TheaterController {

    //Autowiring?


}