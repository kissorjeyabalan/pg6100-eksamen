package no.octopod.cinema.kino.controller

import no.octopod.cinema.kino.ApiTestBase
import no.octopod.cinema.kino.dto.ShowDto
import no.octopod.cinema.kino.repository.ShowRepository
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.octopod.cinema.kino.dto.TheaterDto
import no.octopod.cinema.kino.repository.TheaterRepository
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class ShowApiTest: ApiTestBase() {

    @Autowired
    private lateinit var crudShow: ShowRepository

    @Autowired
    private lateinit var crudTheater: TheaterRepository

    @Before
    fun before() {
        crudShow.deleteAll()
        crudTheater.deleteAll()

        given().get("/shows").then().statusCode(200).body("data.data.size()", CoreMatchers.equalTo(0))
        given().get("/theaters").then().statusCode(200).body("data.data.size()", CoreMatchers.equalTo(0))
    }

    @Test
    fun testCleanDB() {
        RestAssured.given().get("/shows").then()
                .statusCode(200)
                .body("data.data.size()", equalTo(0))
    }

    @Test
    fun testCreateAndGet() {

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = 10
        val movieId = "1"
        val cinemaId = theater.id.toString()
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

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = 10
        val movieId = "1"
        val cinemaId = theater.id.toString()
        val showDto = ShowDto(startTime = startTime, movieId = movieId, cinemaId = cinemaId)

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))

        given().contentType(ContentType.JSON)
                .body(showDto)
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

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = 10
        val movieId = "1"
        val cinemaId = theater.id.toString()
        val dto = ShowDto(startTime, movieId, cinemaId)

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

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = 10
        val movieName = "1"
        val cinemaId = theater.id.toString()
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

        val theater1 = createTheater("theater $movieId1", mutableListOf("a $movieId1"))
        val theater2 = createTheater("theater $movieId2", mutableListOf("a $movieId2"))
        val theater3 = createTheater("theater $movieId3", mutableListOf("a $movieId3"))

        val cinemaId1 = theater1.id.toString()
        val cinemaId2 = theater2.id.toString()
        val cinemaId3 = theater3.id.toString()

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
    fun testGetCatchError() {

        given()
                .param("movie", "a")
                .param("theater", "a")
                .get("/shows")
                .then()
                .statusCode(400)

        given()
                .param("theater", "a")
                .get("/shows")
                .then()
                .statusCode(400)
    }

    @Test
    fun testDeleteById() {

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = 10
        val movieName = "1"
        val cinemaId = theater.id.toString()
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

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = 10
        val movieName = "1"
        val cinemaId = theater.id.toString()
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

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = 10
        val movieName = "1"
        val cinemaId = theater.id.toString()
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
    fun testUpdateErrors() {

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = 10
        val movieName = "1"
        val cinemaId = theater.id.toString()
        val dto1 = ShowDto(startTime, movieName, cinemaId, null)

        given().get("/shows").then().statusCode(200).body("data.data.size()", equalTo(0))

        val path = given()
                .contentType(ContentType.JSON)
                .body(dto1)
                .post("/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val dto = given()
                .get(path)
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
                .put("/shows/a")
                .then()
                .statusCode(404)

        given().contentType(ContentType.JSON)
                .body(dto)
                .put("/shows/1000")
                .then()
                .statusCode(409)

        dto1.id = 1000
        given().contentType(ContentType.JSON)
                .body(dto1)
                .put("/shows/1000")
                .then()
                .statusCode(404)

        dto.startTime = null
        dto.cinemaId = null
        dto.movieId = null

        given().contentType(ContentType.JSON)
                .body(dto)
                .put(path)
                .then()
                .statusCode(400)
    }

    @Test
    fun testPatchTheater() {

        val theater = createTheater("theater", mutableListOf("a1"))

        val startTime = 10
        val movieName = "1"
        val cinemaId = theater.id.toString()
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

    private fun createTheater(name: String, seats: MutableList<String>): TheaterDto {

        val theaterDto = TheaterDto(name = name, seats = seats)




        val path = given().contentType(ContentType.JSON)
                .body(theaterDto)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().get("/theaters").then().statusCode(200).body("data.data.name", hasItem(name))

        given()
                .get(path)
                .then()
                .statusCode(200)
                .body("data.name", equalTo(theaterDto.name))
                .body("data.seats.size()", CoreMatchers.equalTo(theaterDto.seats!!.size))

        return given().get(path)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", TheaterDto::class.java)
    }
}