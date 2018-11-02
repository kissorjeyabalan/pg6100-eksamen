package com.octopod.cinema.kino.controller

import com.octopod.cinema.kino.ApiTestBase
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

class ShowApiTest: ApiTestBase() {

    @Autowired
    private lateinit var crudShow: ShowRepository

    @Before
    fun before() {
        crudShow.deleteAll()
    }

    @Test
    fun testCleanDB() {

        RestAssured.given().get("/shows").then()
                .statusCode(200)
                .body("data.data.size()", equalTo(0))
    }

    @Test
    fun testCreateAndGet() {

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
                .param("theater", cinemaId)
                .get(path)
                .then()
                .statusCode(200)
                .body("data.startTime", equalTo(dto.startTime))
                .body("data.movieName", equalTo(dto.movieName))
                .body("data.cinemaId", equalTo(dto.cinemaId))

    }

    @Test
    fun testCreateAndFailWithNullVariabel() {

        val startTime = null
        val movieName = null
        val cinemaId = null
        val id = null
        val dto = ShowDto(startTime, movieName, cinemaId, id)

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))

        val path = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(400)
                .extract().header("Location")

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))
    }

    @Test
    fun testGetAndFailWithMalformedLimit() {

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
                .param("limit", "0")
                .get("/shows")
                .then()
                .statusCode(400)
    }

    @Test
    fun testGetWithMalformedId() {

        val id = "a"

        given()
                .get("/shows/$id")
                .then()
                .statusCode(404)
    }

    @Test
    fun testDeleteById() {

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
                .delete(path)
                .then()
                .statusCode(204)

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))
    }

    @Test
    fun testDeleteAndFailWithMalformedId() {

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

        val id = "a"

        given()
                .delete("/shows/$id")
                .then()
                .statusCode(404)

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(1))
    }

    @Test
    fun testUpdateShow() {

        val startTime = 10
        val movieName = "movie"
        val cinemaId = "1"
        val dto1 = ShowDto(startTime, movieName, cinemaId, null)

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))

        val path = given().contentType(ContentType.JSON)
                .body(dto1)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val dto = given().get(path)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", ShowDto::class.java)

        dto.movieName = "another name"

        given().contentType(ContentType.JSON)
                .body(dto)
                .put(path)
                .then()
                .statusCode(204)

        given().get(path).then().statusCode(200)
                .body("data.startTime", equalTo(dto.startTime))
                .body("data.movieName", equalTo(dto.movieName))
                .body("data.cinemaId", equalTo(dto.cinemaId))
    }

    @Test
    fun testPatchTheater() {

        val startTime = 10
        val movieName = "movie"
        val cinemaId = "1"
        val dto1 = ShowDto(startTime, movieName, cinemaId, null)

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))

        val path = given().contentType(ContentType.JSON)
                .body(dto1)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val dto = given().get(path)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", ShowDto::class.java)

        val newMovieName = "new movie"
        val body = "{\"movieName\":\"$newMovieName\"}"

        given().contentType(ContentType.JSON)
                .body(body)
                .patch(path)
                .then()
                .statusCode(204)

        given().get(path).then().statusCode(200)
                .body("data.startTime", equalTo(dto.startTime))
                .body("data.", equalTo(newMovieName))
                .body("data.cinemaId", equalTo(dto.cinemaId))
    }
}