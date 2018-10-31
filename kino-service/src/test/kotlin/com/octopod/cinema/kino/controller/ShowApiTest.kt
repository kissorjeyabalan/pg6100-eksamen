package com.octopod.cinema.kino.controller

import com.octopod.cinema.kino.ShowTestBase
import io.restassured.RestAssured
import org.hamcrest.CoreMatchers
import org.junit.Test

class ShowApiTest: ShowTestBase() {

    @Test
    fun testCleanDB() {

        RestAssured.given().get("/shows").then()
                .statusCode(200)
                .body("data.data.size()", CoreMatchers.equalTo(0))
    }
}