package no.octopod.cinema.kino.controller

import no.octopod.cinema.kino.ApiTestBase
import no.octopod.cinema.kino.dto.ShowDto
import no.octopod.cinema.kino.repository.ShowRepository
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
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