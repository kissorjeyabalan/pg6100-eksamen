package no.octopod.cinema.kino.controller

import no.octopod.cinema.kino.ApiTestBase
import no.octopod.cinema.kino.dto.TheaterDto
import no.octopod.cinema.kino.repository.TheaterRepository
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class TheaterApiTest: ApiTestBase() {

    @Autowired
    private lateinit var crudTheater: TheaterRepository

    @Before
    fun before() {
        crudTheater.deleteAll()
    }

    @Test
    fun testCleanDB() {

        RestAssured.given().get("/theaters").then()
                .statusCode(200)
                .body("data.data.size()", equalTo(0))
    }

    @Test
    fun testCreateAndGet() {

        val name = "theater"
        val seats = mutableListOf("a1")
        val dto = TheaterDto(name = name, seats = seats)

        given().get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))

        val path = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(1))

        given()
            .get(path)
            .then()
            .statusCode(200)
            .body("data.name", equalTo(dto.name))
            .body("data.seats.size()", equalTo(seats.size))
    }

    @Test
    fun testCreateAndFailWithNullVariabel() {

        val name = null
        val seatsMax = null
        val id = null
        val dto = TheaterDto(name, seatsMax, id)

        given().get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))

        //val path =
        given().contentType(ContentType.JSON)
                .body(dto)
                .post("/theaters")
                .then()
                .statusCode(400)
                .extract().header("Location")

        given().get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))
    }

    @Test
    fun testGetAndFailWithMalformedLimit() {

        val name = "theater"
        val seats = mutableListOf<String>("a1")
        val dto = TheaterDto(name = name, seats = seats)

        given().get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))

        //val path =
        given().contentType(ContentType.JSON)
                .body(dto)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(1))

        given()
                .param("limit", "0")
                .get("/theaters")
                .then()
                .statusCode(400)
    }

    @Test
    fun testGetWithMalformedId() {

        val id = "a"

        given()
                .get("/theaters/$id")
                .then()
                .statusCode(404)
    }

    @Test
    fun testDeleteById() {

        val name = "theater"
        val seats = mutableListOf<String>("a1")
        val dto = TheaterDto(name = name, seats = seats)

        given().get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))

        val path = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(1))

        given()
                .delete(path)
                .then()
                .statusCode(204)

        given().get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))
    }

    @Test
    fun testDeleteAndFailWithMalformedId() {

        val name = "theater"
        val seats = mutableListOf<String>("a1")
        val dto = TheaterDto(name = name, seats = seats)

        given().get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))

        given().contentType(ContentType.JSON)
                .body(dto)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")
        
        given().get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(1))

        val id = "a"

        given()
                .delete("/theaters/$id")
                .then()
                .statusCode(404)

        given().get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(1))
    }

    @Test
    fun testUpdateTheater() {

        val name1 = "theater"
        val seats = mutableListOf<String>("a1")
        val dto1 = TheaterDto(name = name1, seats = seats)

        given().get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))

        val path = given().contentType(ContentType.JSON)
                .body(dto1)
                .post("/theaters")
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
                .getObject("data", TheaterDto::class.java)

        dto.name = "another name"

        given().contentType(ContentType.JSON)
                .body(dto)
                .put(path)
                .then()
                .statusCode(204)

        given().get(path).then().statusCode(200)
                .body("data.name", equalTo(dto.name))
                .body("data.seatsMax", equalTo(dto.seatsMax))
    }

    @Test
    fun testPatchTheater() {

        val name1 = "theater"
        val seats = mutableListOf<String>("a1")
        val dto1 = TheaterDto(name = name1, seats = seats)

        given().get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))

        val path = given().contentType(ContentType.JSON)
                .body(dto1)
                .post("/theaters")
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
                .getObject("data", TheaterDto::class.java)

        val name = "new name"
        val body = "{\"name\":\"$name\"}"

        given().contentType("application/merge-patch+json")
                .body(body)
                .patch(path)
                .then()
                .statusCode(204)

        given().get(path).then().statusCode(200)
                .body("data.name", equalTo(name))
                .body("data.seatsMax", equalTo(dto.seatsMax))


        // DELETE /shows/{id}/seats/A1

    }
}