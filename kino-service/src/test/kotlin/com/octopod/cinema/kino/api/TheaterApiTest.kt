package com.octopod.cinema.kino.api

import com.octopod.cinema.kino.TheaterTestBase
import com.octopod.cinema.kino.dto.TheaterDto
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
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
}