package no.octopod.cinema.kino.controller

import no.octopod.cinema.kino.ApiTestBase
import no.octopod.cinema.kino.dto.TheaterDto
import no.octopod.cinema.kino.repository.TheaterRepository
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers
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

        RestAssured.given()
                .auth().basic("admin", "admin")
                .get("/theaters").then()
                .statusCode(200)
                .body("data.data.size()", equalTo(0))
    }

    @Test
    fun testCreateAndGet() {

        val name = "theater"
        val seats = mutableListOf("a1")
        val dto = TheaterDto(name = name, seats = seats)

        given()
                .auth().basic("admin", "admin")
                .get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(1))

        given()
                .auth().basic("admin", "admin")
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

        given()
                .auth().basic("admin", "admin")
                .get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))

        //val path =
        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/theaters")
                .then()
                .statusCode(400)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))
    }

    @Test
    fun testGetAndFailWithMalformedLimit() {

        val name = "theater"
        val seats = mutableListOf<String>("a1")
        val dto = TheaterDto(name = name, seats = seats)

        given()
                .auth().basic("admin", "admin")
                .get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))

        //val path =
        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(1))

        given()
                .auth().basic("admin", "admin")
                .param("limit", "0")
                .get("/theaters")
                .then()
                .statusCode(400)
    }

    @Test
    fun testGetWithMalformedId() {

        val id = "a"

        given()
                .auth().basic("admin", "admin")
                .get("/theaters/$id")
                .then()
                .statusCode(404)
    }

    @Test
    fun testDeleteById() {

        val name = "theater"
        val seats = mutableListOf<String>("a1")
        val dto = TheaterDto(name = name, seats = seats)

        given()
                .auth().basic("admin", "admin")
                .get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(1))

        given()
                .auth().basic("admin", "admin")
                .delete(path)
                .then()
                .statusCode(204)

        given()
                .auth().basic("admin", "admin")
                .get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))
    }

    @Test
    fun testDeleteAndFailWithMalformedId() {

        val name = "theater"
        val seats = mutableListOf<String>("a1")
        val dto = TheaterDto(name = name, seats = seats)

        given()
                .auth().basic("admin", "admin")
                .get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")
        
        given()
                .auth().basic("admin", "admin")
                .get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(1))

        val id = "a"

        given()
                .auth().basic("admin", "admin")
                .delete("/theaters/$id")
                .then()
                .statusCode(404)

        given()
                .auth().basic("admin", "admin")
                .get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(1))
    }

    @Test
    fun testUpdateTheater() {

        val name1 = "theater"
        val seats = mutableListOf<String>("a1")
        val dto1 = TheaterDto(name = name1, seats = seats)

        given()
                .auth().basic("admin", "admin")
                .get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto1)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val dto = given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", TheaterDto::class.java)

        dto.name = "another name"

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
                .body("data.name", equalTo(dto.name))
                .body("data.seatsMax", equalTo(dto.seatsMax))
    }

    @Test
    fun testPatchTheater() {

        val name1 = "theater"
        val seats = mutableListOf<String>("a1")
        val dto1 = TheaterDto(name = name1, seats = seats)

        given()
                .auth().basic("admin", "admin")
                .get("/theaters").then().statusCode(200).body("data.data.size()", equalTo(0))

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto1)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val dto = given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", TheaterDto::class.java)

        val name = "new name"
        val body = "{\"name\":\"$name\"}"

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(body)
                .patch(path)
                .then()
                .statusCode(204)

        given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .body("data.name", equalTo(name))
                .body("data.seatsMax", equalTo(dto.seatsMax))
    }

    @Test
    fun testEndpointGetAllAuthorization() {

        given().auth()
                .basic("foo", "123")
                .get("/theaters")
                .then()
                .statusCode(200)

        given().get("/theaters")
                .then()
                .statusCode(200)

        given().auth()
                .basic("admin", "admin")
                .get("/theaters")
                .then()
                .statusCode(200)
    }

    @Test
    fun testEndpointGetSingleAuthorization() {

        given().auth()
                .basic("foo", "123")
                .get("/theaters/1")
                .then()
                .statusCode(Matchers.not(403))

        given().get("/theaters/1")
                .then()
                .statusCode(Matchers.not(403))

        given().auth()
                .basic("admin", "admin")
                .get("/theaters/1")
                .then()
                .statusCode(Matchers.not(403))
    }

    @Test
    fun testEndpointPostAuthorization() {

        given()
                .auth()
                .basic("foo", "123")
                .post("/theaters/1")
                .then()
                .statusCode(403)

        given()
                .post("/theaters/1")
                .then()
                .statusCode(401)

        given()
                .auth()
                .basic("admin", "admin")
                .post("/theaters/1")
                .then()
                .statusCode(Matchers.not(403))
    }

    @Test
    fun testEndpointDeleteAuthorization() {

        given()
                .auth()
                .basic("foo", "123")
                .delete("/theaters/1")
                .then()
                .statusCode(403)

        given()
                .delete("/theaters/1")
                .then()
                .statusCode(401)

        given()
                .auth()
                .basic("admin", "admin")
                .delete("/theaters/1")
                .then()
                .statusCode(Matchers.not(403))
    }

    @Test
    fun testEndpointPutAuthorization() {

        given()
                .auth()
                .basic("foo", "123")
                .put("/theaters/1")
                .then()
                .statusCode(403)

        given()
                .put("/theaters/1")
                .then()
                .statusCode(401)

        given()
                .auth()
                .basic("admin", "admin")
                .put("/theaters/1")
                .then()
                .statusCode(Matchers.not(403))
    }

    @Test
    fun testEndpointPatchAuthorization() {

        given()
                .auth()
                .basic("foo", "123")
                .contentType("application/merge-patch+json")
                .patch("/theaters/1")
                .then()
                .statusCode(403)

        given()
                .contentType("application/merge-patch+json")
                .patch("/theaters/1")
                .then()
                .statusCode(401)

        given()
                .auth()
                .basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .patch("/theaters/1")
                .then()
                .statusCode(Matchers.not(403))
    }
}