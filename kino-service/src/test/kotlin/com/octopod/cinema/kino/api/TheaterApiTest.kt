package com.octopod.cinema.kino.api

import com.octopod.cinema.kino.TheaterTestBase
import com.octopod.cinema.kino.show.dto.TheaterDto
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test

class TheaterApiTest : TheaterTestBase() {

    @Test
    fun testCleanDB() {

        RestAssured.given().get().then()
                .statusCode(200)
                .body("size()", equalTo(0))
    }

    @Test
    fun testCreateAndGet() {

        val name = "theater"
        val seatsMax = 10
        val seatsEmpty = 10
        val dto = TheaterDto(name, seatsMax, seatsEmpty, null)

        given().get().then().statusCode(200).body("size()", equalTo(0))

        val id = given().contentType(ContentType.JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(201)
                .extract().asString()

        given().get().then().statusCode(200).body("size()", equalTo(1))

        given().pathParam("id", id)
                .get("/id/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo(name))
                .body("seatsMax", equalTo(seatsMax))
                .body("seatsEmpty", equalTo(seatsEmpty))
    }
}