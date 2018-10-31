package com.octopod.cinema.kino

import com.octopod.cinema.kino.repository.ShowRepository
import io.restassured.RestAssured
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [(ShowApplication::class)],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class ShowTestBase {

    @LocalServerPort private var port = 0

    @Autowired
    private lateinit var crud: ShowRepository

    @Before
    fun clean() {

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        crud.deleteAll()
    }
}