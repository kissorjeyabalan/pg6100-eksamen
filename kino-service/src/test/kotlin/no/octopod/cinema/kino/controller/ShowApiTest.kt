package no.octopod.cinema.kino.controller

import com.fasterxml.jackson.databind.ObjectMapper
import no.octopod.cinema.kino.ApiTestBase
import no.octopod.cinema.common.dto.ShowDto
import no.octopod.cinema.kino.repository.ShowRepository
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.octopod.cinema.common.dto.TheaterDto
import no.octopod.cinema.kino.repository.TheaterRepository
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZonedDateTime

class ShowApiTest: ApiTestBase() {

    @Autowired
    private lateinit var crudShow: ShowRepository

    @Autowired
    private lateinit var crudTheater: TheaterRepository

    @Before
    fun before() {
        crudShow.deleteAll()
        crudTheater.deleteAll()

        given().auth().basic("admin", "admin").get("/shows").then().statusCode(200).body("data.data.size()", CoreMatchers.equalTo(0))
        given().auth().basic("admin", "admin").get("/theaters").then().statusCode(200).body("data.data.size()", CoreMatchers.equalTo(0))
    }

    @Test
    fun testCleanDB() {
        RestAssured.given()
                .auth().basic("admin", "admin")
                .get("/shows")
                .then()
                .statusCode(200)
                .body("data.data.size()", equalTo(0))
    }

    @Test
    fun testCreateAndGet() {

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = ZonedDateTime.now().withNano(0)
        val movieId = 1L
        val cinemaId = theater.id
        val dto = ShowDto(startTime, movieId, cinemaId)

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows")
                .then()
                .statusCode(200)
                .body("data.data[0].startTime", equalTo(startTime.toOffsetDateTime().toString()))
                .body("data.data.size()", equalTo(1))

        given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .body("data.startTime", notNullValue())
                .body("data.movieId", equalTo(dto.movieId?.toInt()))
                .body("data.cinemaId", equalTo(dto.cinemaId?.toInt()))
    }

    @Test
    fun testCreateAndGetWithTheaterIdAndMovieId() {

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = ZonedDateTime.now().withNano(0)
        val movieId = 1L
        val cinemaId = theater.id
        val showDto = ShowDto(startTime = startTime, movieId = movieId, cinemaId = cinemaId)

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(showDto)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows")
                .then()
                .statusCode(200)
                .body("data.data.startTime", notNullValue())
                .body("data.data.size()", equalTo(1))

        given()
                .auth().basic("admin", "admin")
                .param("theater", cinemaId)
                .param("movie", movieId)
                .get("/shows").then().statusCode(200)
                .body("data.data.size()", equalTo(1))
    }

    @Test
    fun testCreateAndGetWithTheaterId() {

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = ZonedDateTime.now().withNano(0)
        val movieId = 1L
        val cinemaId = theater.id
        val dto = ShowDto(startTime, movieId, cinemaId)

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows")
                .then().statusCode(200)
                .body("data.data.startTime", notNullValue())
                .body("data.data.size()", equalTo(1))

        given()
                .auth().basic("admin", "admin")
                .param("theater", cinemaId)
                .get("/shows").then()
                .statusCode(200)
                .body("data.data.size()", equalTo(1))
    }

    @Test
    fun testCreateAndFailWithNullVariable() {

        val dto = ShowDto()

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(400)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))

        dto.startTime = ZonedDateTime.now().withNano(0)
        dto.cinemaId = 1L
        dto.movieId = 1L

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(400)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))
    }

    @Test
    fun testGetAndFailWithMalformedLimit() {

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = ZonedDateTime.now().withNano(0)
        val movieName = 1L
        val cinemaId = theater.id
        val dto = ShowDto(startTime, movieName, cinemaId)

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows")
                .then().statusCode(200)
                .body("data.data[0].startTime", notNullValue())
                .body("data.data.size()", equalTo(1))

        given()
                .auth().basic("admin", "admin")
                .param("limit", "0")
                .get("/shows")
                .then()
                .statusCode(400)
    }

    @Test
    fun testGetWithNonExistentTheater() {

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = ZonedDateTime.now().withNano(0)
        val movieName = 1L
        val cinemaId = theater.id
        val dto = ShowDto(startTime, movieName, cinemaId)

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows")
                .then().statusCode(200)
                .body("data.data[0].startTime", notNullValue())
                .body("data.data.size()", equalTo(1))

        val badId = 1000L

        given()
                .auth().basic("admin", "admin")
                .get("/shows/$badId")
                .then()
                .statusCode(404)
    }

    @Test
    fun testGetWithMalformedId() {

        val id = "a"

        given()
                .auth().basic("admin", "admin")
                .get("/shows/$id")
                .then()
                .statusCode(404)
    }

    @Test
    fun testGetWithHalpage() {

        val startTime1 = ZonedDateTime.now().withNano(0)
        val startTime2 = ZonedDateTime.now().withNano(0)
        val startTime3 = ZonedDateTime.now().withNano(0)

        val movieId1 = 1L
        val movieId2 = 4L
        val movieId3 = 7L

        val theater1 = createTheater("theater $movieId1", mutableListOf("a $movieId1"))
        val theater2 = createTheater("theater $movieId2", mutableListOf("a $movieId2"))
        val theater3 = createTheater("theater $movieId3", mutableListOf("a $movieId3"))

        val cinemaId1 = theater1.id
        val cinemaId2 = theater2.id
        val cinemaId3 = theater3.id

        val dto1 = ShowDto(startTime1, movieId1, cinemaId1)
        val dto2 = ShowDto(startTime2, movieId2, cinemaId2)
        val dto3 = ShowDto(startTime3, movieId3, cinemaId3)

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto1)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto2)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto3)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows").then().statusCode(200).body("data.data.size()", equalTo(3))

        given()
                .auth().basic("admin", "admin")
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
                .auth().basic("admin", "admin")
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
                .auth().basic("admin", "admin")
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
    fun testGetCatchError() {

        given()
                .auth().basic("admin", "admin")
                .param("movie", "a")
                .param("theater", "a")
                .get("/shows")
                .then()
                .statusCode(400)

        given()
                .auth().basic("admin", "admin")
                .param("theater", "a")
                .get("/shows")
                .then()
                .statusCode(400)
    }

    @Test
    fun testDeleteById() {

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = ZonedDateTime.now().withNano(0)
        val movieName = 1L
        val cinemaId = theater.id
        val dto = ShowDto(startTime, movieName, cinemaId)

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows")
                .then().statusCode(200)
                .body("data.data[0].startTime", notNullValue())
                .body("data.data.size()", equalTo(1))

        given()
                .auth().basic("admin", "admin")
                .delete(path)
                .then()
                .statusCode(204)

        given()
                .auth().basic("admin", "admin")
                .get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))
    }

    @Test
    fun testDeleteAndFailWithMalformedId() {

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = ZonedDateTime.now().withNano(0)
        val movieName = 1L
        val cinemaId = theater.id
        val dto = ShowDto(startTime, movieName, cinemaId)

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows")
                .then().statusCode(200)
                .body("data.data.startTime", notNullValue())
                .body("data.data.size()", equalTo(1))

        val id = "a"

        given()
                .auth().basic("admin", "admin")
                .delete("/shows/$id")
                .then()
                .statusCode(404)

        given()
                .auth().basic("admin", "admin")
                .get("/shows").then().statusCode(200).body("data.data.size()", equalTo(1))
    }

    @Test
    fun testDeleteSeatFromShow() {

        val theater = createTheater("theater", mutableListOf("a1", "a2", "b1", "b2"))

        val startTime = ZonedDateTime.now().withNano(0)
        val movieName = 1L
        val cinemaId = theater.id
        val dto = ShowDto(startTime, movieName, cinemaId)

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get(path)
                .then().statusCode(200)
                .body("data.startTime", notNullValue())

        val pathDto = given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", ShowDto::class.java)

        given()
                .auth().basic("admin", "admin")
                .delete("/shows/${pathDto.id}/seats/a1")
                .then()
                .statusCode(204)

        given()
                .auth().basic("admin", "admin")
                .get("/shows/${pathDto.id}").then().statusCode(200).body("data.availableSeats.size()", equalTo(3))
    }

    @Test
    fun testAndFailDeleteSeatFromShow() {

        val seats = "a1"

        val theater = createTheater("theater", mutableListOf(seats))

        val startTime = ZonedDateTime.now().withNano(0)
        val movieName = 1L
        val cinemaId = theater.id
        val dto = ShowDto(startTime, movieName, cinemaId)

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows")
                .then().statusCode(200)
                .body("data.data.startTime", notNullValue())
                .body("data.data.size()", equalTo(1))

        val pathDto = given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", ShowDto::class.java)

        given()
                .auth().basic("admin", "admin")
                .delete("/shows/a/seats/a1")
                .then()
                .statusCode(404)

        //Testing for seat with bad id
        given()
                .auth().basic("admin", "admin")
                .delete("/shows/a/seats/a1")
                .then()
                .statusCode(404)

        given()
                .auth().basic("admin", "admin")
                .delete("/shows/${pathDto.id}/seats/$seats")
                .then()
                .statusCode(204)

        //Testing if you can delete already deleted seat
        given()
                .auth().basic("admin", "admin")
                .delete("/shows/${pathDto.id}/seats/a1")
                .then()
                .statusCode(404)

        //Testing if you can delete from a show that does not exists
        given()
                .auth().basic("admin", "admin")
                .delete("/shows/1000/seats/a1")
                .then()
                .statusCode(404)
    }

    @Test
    fun testUpdateShow() {

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = ZonedDateTime.now().withNano(0)
        val movieId = 1L
        val cinemaId = theater.id
        val dto1 = ShowDto(startTime, movieId, cinemaId)

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto1)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows")
                .then().statusCode(200)
                .body("data.data.startTime", notNullValue())
                .body("data.data.size()", equalTo(1))

        val dto = given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", ShowDto::class.java)

        dto.movieId = 2L

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .put(path)
                .then()
                .statusCode(204)

        given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .body("data.startTime", notNullValue())
                .body("data.movieId", equalTo(dto.movieId?.toInt()))
                .body("data.cinemaId", equalTo(cinemaId?.toInt()))
    }

    @Test
    fun testUpdateErrors() {

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = ZonedDateTime.now().withNano(0)
        val movieName = 1L
        val cinemaId = theater.id
        val dto1 = ShowDto(startTime, movieName, cinemaId)

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto1)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows")
                .then().statusCode(200)
                .body("data.data.startTime", notNullValue())
                .body("data.data.size()", equalTo(1))

        val dto = given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", ShowDto::class.java)

        dto.movieId = 2L

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .put("/shows/a")
                .then()
                .statusCode(404)

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .put("/shows/1000")
                .then()
                .statusCode(409)

        dto1.id = 1000L

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto1)
                .put("/shows/1000")
                .then()
                .statusCode(404)

        dto.startTime = null
        dto.cinemaId = null
        dto.movieId = null

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .put(path)
                .then()
                .statusCode(400)
    }

    @Test
    fun testPatchTheater() {

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = ZonedDateTime.now().withNano(0)
        val movieName = 1L
        val cinemaId = theater.id
        val dto1 = ShowDto(startTime = startTime, movieId = movieName, cinemaId = cinemaId, availableSeats = theater.seats)

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto1)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows")
                .then().statusCode(200)
                .body("data.data[0].startTime", notNullValue())
                .body("data.data.size()", equalTo(1))

        val dto = given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", ShowDto::class.java)

        val newMovieName = 3L
        val body = "{\"movieId\":$newMovieName}"

        given()
                .auth().basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body(body)
                .patch(path)
                .then()
                .statusCode(204)

        given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .body("data.startTime", notNullValue())
                .body("data.movieId", equalTo(newMovieName.toInt()))
                .body("data.cinemaId", equalTo(cinemaId?.toInt()))
                .body("data.availableSeats", equalTo(dto.availableSeats))
    }

    @Test
    fun testpatchAndFail() {

        //Test with malformed id
        given()
                .auth().basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("{\"fail\":\"test\"}")
                .patch("/shows/a")
                .then()
                .statusCode(404)

        //Test path with non existent show
        given()
                .auth().basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("{\"fail\":\"test\"}")
                .patch("/shows/1")
                .then()
                .statusCode(404)

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = ZonedDateTime.now().withNano(0)
        val movieName = 1L
        val cinemaId = theater.id
        val dto1 = ShowDto(startTime, movieName, cinemaId)

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto1)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows")
                .then().statusCode(200)
                .body("data.data.startTime", notNullValue())
                .body("data.data.size()", equalTo(1))

        //Testing if valid json
        given()
                .auth().basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("fail\":\"test\"}")
                .patch(path)
                .then()
                .statusCode(400)

        //testing if we can send id
        given()
                .auth().basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("{\"id\":\"123\"}")
                .patch(path)
                .then()
                .statusCode(409)

        val newTheater = createTheater("new theater", mutableListOf("b2", "b1"))

        val newMovieId = 2L
        val newStarttime = ZonedDateTime.now().withNano(0)
        val newCinemaId = newTheater.id
        val newSeats = newTheater.seats

        val mapper = ObjectMapper()

        val body = "{" +
                "\"startTime\":\"$newStarttime\", " +
                "\"cinemaId\":$newCinemaId, " +
                "\"movieId\":$newMovieId, " +
                "\"availableSeats\":${mapper.writeValueAsString(newSeats)}}"

        //Testing that all json merges work
        given()
                .auth().basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body(body)
                .patch(path)
                .then()
                .statusCode(204)

        val newBadMovieName = "a"
        val newBadStartTime = "a"
        val newBadCinemaId = "a"

        given()
                .auth().basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("{\"startTime\":\"$newBadStartTime\"}")
                .patch(path)
                .then()
                .statusCode(400)

        given()
                .auth().basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("{\"movieId\":\"$newBadMovieName\"}")
                .patch(path)
                .then()
                .statusCode(400)

        given()
                .auth().basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("{\"cinemaId\":\"$newBadCinemaId\"}")
                .patch(path)
                .then()
                .statusCode(400)

        val badSeats1 = null
        val badSeats2 = 5L

        given()
                .auth().basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("{\"availableSeats\":$badSeats1}")
                .patch(path)
                .then()
                .statusCode(400)

        given()
                .auth().basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("{\"availableSeats\":${mapper.writeValueAsString(badSeats2)}}")
                .patch(path)
                .then()
                .statusCode(400)
    }

    private fun createTheater(name: String, seats: MutableList<String>): TheaterDto {

        val theaterDto = TheaterDto(name = name, seats = seats)

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(theaterDto)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/theaters").then().statusCode(200).body("data.data.name", hasItem(name))

        given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .body("data.name", equalTo(theaterDto.name))
                .body("data.seats.size()", CoreMatchers.equalTo(theaterDto.seats!!.size))

        return given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", TheaterDto::class.java)
    }

    @Test
    fun testEndpointGetAllAuthorization() {

        given().auth()
                .basic("foo", "123")
                .get("/shows")
                .then()
                .statusCode(200)

        given().get("/shows")
                .then()
                .statusCode(200)

        given().auth()
                .basic("admin", "admin")
                .get("/shows")
                .then()
                .statusCode(200)
    }

    @Test
    fun testEndpointGetSingleAuthorization() {

        given().auth()
                .basic("foo", "123")
                .get("/shows/1")
                .then()
                .statusCode(not(403))

        given().get("/shows/1")
                .then()
                .statusCode(not(403))

        given().auth()
                .basic("admin", "admin")
                .get("/shows/1")
                .then()
                .statusCode(not(403))
    }

    /*@Test
    fun testEndpointPostAuthorization() {

        given()
                .auth()
                .basic("foo", "123")
                .post("/shows/1")
                .then()
                .statusCode(403)

        given()
                .post("/shows/1")
                .then()
                .statusCode(401)

        given()
                .auth()
                .basic("admin", "admin")
                .post("/shows/1")
                .then()
                .statusCode(not(403))
    }*/

    /*@Test
    fun testEndpointDeleteAuthorization() {

        given()
                .auth()
                .basic("foo", "123")
                .delete("/shows/1")
                .then()
                .statusCode(403)

        given()
                .delete("/shows/1")
                .then()
                .statusCode(401)

        given()
                .auth()
                .basic("admin", "admin")
                .delete("/shows/1")
                .then()
                .statusCode(not(403))
    }
*/
    @Test
    fun testEndpointPutAuthorization() {

        given()
                .auth()
                .basic("foo", "123")
                .put("/shows/1")
                .then()
                .statusCode(403)

        given()
                .put("/shows/1")
                .then()
                .statusCode(401)

        given()
                .auth()
                .basic("admin", "admin")
                .put("/shows/1")
                .then()
                .statusCode(not(403))
    }

    @Test
    fun testEndpointPatchAuthorization() {

        given()
                .auth()
                .basic("foo", "123")
                .contentType("application/merge-patch+json")
                .patch("/shows/1")
                .then()
                .statusCode(403)

        given()
                .contentType("application/merge-patch+json")
                .patch("/shows/1")
                .then()
                .statusCode(401)

        given()
                .auth()
                .basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .patch("/shows/1")
                .then()
                .statusCode(not(403))
    }

   /* @Test
    fun testEndpointDeleteSeatAuthorization() {

        /*given()
                .auth()
                .basic("foo", "123")
                .contentType("application/merge-patch+json")
                .delete("/shows/1/seats/1")
                .then()
                .statusCode(403)*/

        given()
                .contentType("application/merge-patch+json")
                .delete("/shows/1/seats/1")
                .then()
                .statusCode(401)

        given()
                .auth()
                .basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .delete("/shows/1/seats/1")
                .then()
                .statusCode(not(403))
    }*/

    @Test
    fun testPostSeatInShow() {

        val theater = createTheater("theater", mutableListOf("a1", "a2", "b1", "b2"))

        val startTime = ZonedDateTime.now().withNano(0)
        val movieName = 1L
        val cinemaId = theater.id
        val dto = ShowDto(startTime, movieName, cinemaId)

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows")
                .then().statusCode(200)
                .body("data.data.startTime", notNullValue())
                .body("data.data.size()", equalTo(1))

        val pathDto = given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", ShowDto::class.java)

        given()
                .auth().basic("admin", "admin")
                .delete("/shows/${pathDto.id}/seats/b2")
                .then()
                .statusCode(204)

        given()
                .auth().basic("admin", "admin")
                .get("/shows/${pathDto.id}").then().statusCode(200).body("data.availableSeats.size()", equalTo(3))

        given()
                .auth().basic("admin", "admin")
                .post("/shows/${pathDto.id}/seats/b2")
                .then()
                .statusCode(204)

        given()
                .auth().basic("admin", "admin")
                .get("/shows/${pathDto.id}").then().statusCode(200).body("data.availableSeats.size()", equalTo(4))
    }

    @Test
    fun testPostAndFailSeatInShow() {

        val theater = createTheater("theater", mutableListOf("a1", "a2", "b1", "b2"))

        val startTime = ZonedDateTime.now().withNano(0)
        val movieName = 1L
        val cinemaId = theater.id
        val dto = ShowDto(startTime, movieName, cinemaId)

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/shows")
                .then().statusCode(200)
                .body("data.data.startTime", notNullValue())
                .body("data.data.size()", equalTo(1))

        val pathDto = given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", ShowDto::class.java)

        given()
                .auth().basic("admin", "admin")
                .post("/shows/${pathDto.id}/seats/a1")
                .then()
                .statusCode(409)

        given()
                .auth().basic("admin", "admin")
                .get("/shows/${pathDto.id}").then().statusCode(200).body("data.availableSeats.size()", equalTo(4))
    }
}