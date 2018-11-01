package com.octopod.cinema.kino.controller

import com.octopod.cinema.kino.ApiTestBase
import com.octopod.cinema.kino.ShowApplication
import com.octopod.cinema.kino.dto.ShowDto
import com.octopod.cinema.kino.dto.TheaterDto
import com.octopod.cinema.kino.repository.ShowRepository
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [(ShowApplication::class)],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShowApiTest {

    @Autowired
    private lateinit var crudShow: ShowRepository

    @LocalServerPort
    private var port = 0

    @Before
    fun before() {
        crudShow.deleteAll()

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @Test
    fun testCleanDB() {

        RestAssured.given().get("/shows").then()
                .statusCode(200)
                .body("data.data.size()", CoreMatchers.equalTo(0))
    }

    @Test
    fun testCreateAndGet() {
        /*
        //TODO: finn ut om det er nødvendig med sikkerhet for å skjekke om cinemaId er til en faktisk kino
        val startTime = 10
        val movieName = "movie"
        val cinemaId = "1"
        val dto = ShowDto(startTime, movieName, cinemaId, null)

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))


        val path = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")



        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(1))

        given()
                .get(path)
                .then()
                .statusCode(200)
                .body("data.startTime", equalTo(dto.startTime))
                .body("data.movieName", equalTo(dto.movieName))
                .body("data.cinemaId", equalTo(dto.cinemaId))
                */
    }
}