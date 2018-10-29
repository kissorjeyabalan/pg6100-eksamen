package com.octopod.cinema.kino.controller

import com.octopod.cinema.kino.TheaterTestBase
import com.octopod.cinema.kino.dto.TheaterDto
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Test

class TheaterApiTest: TheaterTestBase() {

    @Test
    fun testCleanDB() {

        RestAssured.given().get("/theaters").then()
                .statusCode(200)
                .body("data.size()", equalTo(0))
    }

    @Test
    fun testCreateAndGet() {

        val name = "theater"
        val seatsMax = 10
        val dto = TheaterDto(name, seatsMax, null)

        given().get("/theaters").then().statusCode(200).body("data.size()", equalTo(0))


        val path = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")



        given().get("/theaters").then().statusCode(200).body("data.size()", equalTo(1))

        given()
            .get(path)
            .then()
            .statusCode(200)
            .body("data.name", equalTo(dto.name))
            .body("data.seatsMax", equalTo(seatsMax))
    }

    @Test
    fun testCreateAndFailWithNullVariabel() {

        val name = null
        val seatsMax = null
        val dto = TheaterDto(name, seatsMax, null)

        given().get("/theaters").then().statusCode(200).body("data.size()", equalTo(0))

        val path = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/theaters")
                .then()
                .statusCode(400)
                .extract().header("Location")

        given().get("/theaters").then().statusCode(200).body("data.size()", equalTo(1))
    }

    @Test
    fun testGetAndFailWithMalformedLimit() {

        val name = "theater"
        val seatsMax = 10
        val dto = TheaterDto(name, seatsMax, null)

        given().get("/theaters").then().statusCode(200).body("data.size()", equalTo(0))


        val path = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")



        given().get("/theaters").then().statusCode(200).body("data.size()", equalTo(1))

        given()
                .formParam("limit", 0)
                .get(path)
                .then()
                .statusCode(400)
    }

    @Test
    fun testDeleteById() {

        val name = "theater"
        val seatsMax = 10
        val dto = TheaterDto(name, seatsMax, null)

        given().get("/theaters").then().statusCode(200).body("data.size()", equalTo(0))


        val path = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")



        given().get("/theaters").then().statusCode(200).body("data.size()", equalTo(1))

        given()
                .delete(path)
                .then()
                .statusCode(204)

        given().get("/theaters").then().statusCode(200).body("data.size()", equalTo(0))
    }

    @Test
    fun testDeleteAndFailWithMalformedId() {

        val name = "theater"
        val seatsMax = 10
        val dto = TheaterDto(name, seatsMax, null)

        given().get("/theaters").then().statusCode(200).body("data.size()", equalTo(0))

        val path = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")



        given().get("/theaters").then().statusCode(200).body("data.size()", equalTo(1))

        given()
                .body("a")
                .delete()
                .then()
                .statusCode(404)

        given().get("/theaters").then().statusCode(200).body("data.size()", equalTo(1))
    }

    @Test
    fun updateTheater() {

        val name1 = "theater"
        val seatsMax1 = 10
        val dto1 = TheaterDto(name1, seatsMax1, null)

        given().get("/theaters").then().statusCode(200).body("data.size()", equalTo(0))

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
    fun patchTheater() {

        val name1 = "theater"
        val seatsMax1 = 10
        val dto1 = TheaterDto(name1, seatsMax1, null)

        given().get("/theaters").then().statusCode(200).body("data.size()", equalTo(0))

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

        given().contentType(ContentType.JSON)
                .body(body)
                .patch(path)
                .then()
                .statusCode(204)

        given().get(path).then().statusCode(200)
                .body("data.name", equalTo(name))
                .body("data.seatsMax", equalTo(dto.seatsMax))

    }
}