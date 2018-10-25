package com.octopod.cinema.kino

import com.octopod.cinema.kino.show.dto.TheaterDto
import com.octopod.cinema.kino.theater.TheaterApplication
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [(TheaterApplication::class)],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class TheaterTestBase {

    @LocalServerPort private var port = 0

    //Taken fram arcuri82 - NRv2 test
    @Before
    fun clean() {
        
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/cinema/api/theater"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        val list = given().accept(ContentType.JSON).get()
                .then()
                .statusCode(200)
                .extract()
                .`as`(Array<TheaterDto>::class.java)
                .toList()

        list.stream().forEach {
            given().pathParam("id", it.id)
                    .delete("/{id}")
                    .then()
                    .statusCode(204)
        }

        given().get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(0))

    }
}