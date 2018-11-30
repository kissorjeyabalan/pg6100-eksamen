package com.octopod.cinema.kino.controller

import com.octopod.cinema.kino.ApiTestBase
import com.octopod.cinema.kino.dto.ShowDto
import com.octopod.cinema.kino.repository.ShowRepository
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

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

        val startTime = 10
        val movieId = "1"
        val cinemaId = "1"
        val dto = ShowDto(startTime, movieId, cinemaId, null)

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
                .body("data.movieId", equalTo(dto.movieId))
                .body("data.cinemaId", equalTo(dto.cinemaId))
    }

    @Test
    fun testCreateAndGetWithTheaterIdAndMovieId() {

        val startTime = 10
        val movieId = "1"
        val cinemaId = "1"
        val dto = ShowDto(startTime, movieId, cinemaId, null)

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))

        given().contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .param("theater", cinemaId)
                .param("movie", movieId)
                .get("/shows").then().statusCode(200)
                .body("data.data.size()", equalTo(1))
    }

    @Test
    fun testCreateAndGetWithTheaterId() {

        val startTime = 10
        val movieId = "1"
        val cinemaId = "1"
        val dto = ShowDto(startTime, movieId, cinemaId, null)

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))

        given().contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .param("theater", cinemaId)
                .get("/shows").then()
                .statusCode(200)
                .body("data.data.size()", equalTo(1))
    }

    @Test
    fun testCreateAndFailWithNullVariabel() {

        val startTime = null
        val movieName = null
        val cinemaId = null
        val id = null
        val dto = ShowDto(startTime, movieName, cinemaId, id)

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))

        given().contentType(ContentType.JSON)
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
        val movieName = "1"
        val cinemaId = "1"
        val dto = ShowDto(startTime, movieName, cinemaId, null)

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))

        given().contentType(ContentType.JSON)
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
    fun testGetWithHalpage() {

        val startTime1 = 10
        val startTime2 = 20
        val startTime3 = 30

        val movieId1 = "1"
        val movieId2 = "4"
        val movieId3 = "7"

        val cinemaId1 = "2"
        val cinemaId2 = "4"
        val cinemaId3 = "6"

        val dto1 = ShowDto(startTime1, movieId1, cinemaId1, null)
        val dto2 = ShowDto(startTime2, movieId2, cinemaId2, null)
        val dto3 = ShowDto(startTime3, movieId3, cinemaId3, null)

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))

        given().contentType(ContentType.JSON)
                .body(dto1)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().contentType(ContentType.JSON)
                .body(dto2)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().contentType(ContentType.JSON)
                .body(dto3)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(3))

        given()
                .param("limit", "1")
                .get("/shows")
                .then()
                .statusCode(200)
                .body("data.pages", equalTo(3))
                .body("data.count", equalTo(3))
                .body("data.data.size()", equalTo(1))
                .body("$", not(hasKey("data._links.previous")))
                .body("data._links.next.href", notNullValue())
                .body("data._links.self.href", notNullValue())

        given()
                .param("limit", "1")
                .param("page", "2")
                .get("/shows")
                .then()
                .statusCode(200)
                .body("data.pages", equalTo(3))
                .body("data.count", equalTo(3))
                .body("data.data.size()", equalTo(1))
                .body("data._links.previous.href", notNullValue())
                .body("data._links.next.href", notNullValue())
                .body("data._links.self.href", notNullValue())

        given()
                .param("limit", "1")
                .param("page", "3")
                .get("/shows")
                .then()
                .statusCode(200)
                .body("data.pages", equalTo(3))
                .body("data.count", equalTo(3))
                .body("data.data.size()", equalTo(1))
                .body("data._links.previous", notNullValue())
                .body("$", not(hasKey("data._links.next")))
                .body("data._links.self.href", notNullValue())
    }

    @Test
    fun testDeleteById() {

        val startTime = 10
        val movieName = "1"
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
        val movieName = "1"
        val cinemaId = "1"
        val dto = ShowDto(startTime, movieName, cinemaId, null)

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))

        given().contentType(ContentType.JSON)
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
        val movieName = "1"
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

        dto.movieId = "2"

        given().contentType(ContentType.JSON)
                .body(dto)
                .put(path)
                .then()
                .statusCode(204)

        given().get(path).then().statusCode(200)
                .body("data.startTime", equalTo(dto.startTime))
                .body("data.movieId", equalTo(dto.movieId))
                .body("data.cinemaId", equalTo(dto.cinemaId))
    }

    @Test
    fun testPatchTheater() {

        val startTime = 10
        val movieName = "1"
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

        val newMovieName = "3"
        val body = "{\"movieId\":\"$newMovieName\"}"

        given().contentType(ContentType.JSON)
                .body(body)
                .patch(path)
                .then()
                .statusCode(204)

        given().get(path).then().statusCode(200)
                .body("data.startTime", equalTo(dto.startTime))
                .body("data.movieId", equalTo(newMovieName))
                .body("data.cinemaId", equalTo(dto.cinemaId))
    }
}