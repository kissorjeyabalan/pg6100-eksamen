package com.octopod.cinema.kino.api

import com.octopod.cinema.common.dto.WrappedResponse
import com.octopod.cinema.kino.TheaterTestBase
import com.octopod.cinema.kino.dto.TheaterDto
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

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
        val seatsEmpty = 10
        val dto = TheaterDto(name, seatsMax, seatsEmpty, null)

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
            .body("data.seatsEmpty", equalTo(seatsEmpty))
    }

    @Test
    fun testDeleteById() {

        val name = "theater"
        val seatsMax = 10
        val seatsEmpty = 10
        val dto = TheaterDto(name, seatsMax, seatsEmpty, null)

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
    fun updateTheater() {

        val name1 = "theater"
        val seatsMax1 = 10
        val seatsEmpty1 = 10
        val dto1 = TheaterDto(name1, seatsMax1, seatsEmpty1, null)

        /*
        val name2 = "theater"
        val seatsMax2 = 10
        val seatsEmpty2 = 10
        val dto2 = TheaterDto(name2, seatsMax2, seatsEmpty2, null)
*/

        given().get("/theaters").then().statusCode(200).body("data.size()", equalTo(0))

        val path = given().contentType(ContentType.JSON)
                .body(dto1)
                .post("/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val wrappedResponse = given().get(path)
                .then()
                .statusCode(200)
                .extract()
                .`as`(JsonNode::class.java)

        val mapper = ObjectMapper()
        val dto = mapper.readValue(
                mapper.treeAsTokens(wrappedResponse),

        )

        val dto = wrappedResponseData as TheaterDto

        given().contentType(ContentType.JSON)
                .body(dto)
                .put(path)
                .then()
                .statusCode(204)
                .body("data.name", equalTo(dto.name))
    }
}