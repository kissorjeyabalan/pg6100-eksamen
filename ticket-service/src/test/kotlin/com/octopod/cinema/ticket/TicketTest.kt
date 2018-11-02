package com.octopod.cinema.ticket

import com.octopod.cinema.common.dto.TicketDto
import com.octopod.cinema.ticket.repository.TicketRepository
import io.restassured.RestAssured
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.*
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.LocalServerPort

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TicketTest {

    @Autowired
    private lateinit var repo: TicketRepository

    @LocalServerPort
    protected var port = 0

    @Before
    fun initialize() {

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/tickets"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        repo.deleteAll()
    }


    @Test
    fun testCreateAndGet() {

        val userId = "1"
        val screeningId = "1"

        val dto = TicketDto(userId, screeningId,null, null)

        val id = given().contentType(ContentType.JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(201)

        given().get()
                .then()
                .statusCode(200)
                .body("data.list.size()", equalTo(1))



    }


    @Test
    fun testDeleteById() {
        val ticketId = "1"
        val userId = "1"
        val screeningId = "1"

        val dto = TicketDto(userId, screeningId,null, ticketId)

        given().get()
                .then()
                .statusCode(200)
                .body("data.list.size()", equalTo(1))


        val id = given().contentType(ContentType.JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().get()
                .then()
                .statusCode(200)
                .body("data.data.size()", equalTo(1))

        given().delete(id)
                .then()
                .statusCode(204)

        given().get()
                .then()
                .statusCode(200)
                .body("data.data.size()", equalTo(0))

    }

}