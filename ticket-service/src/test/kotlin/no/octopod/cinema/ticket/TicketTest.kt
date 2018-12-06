package no.octopod.cinema.ticket

import no.octopod.cinema.common.dto.TicketDto
import no.octopod.cinema.ticket.repository.TicketRepository
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
import java.time.ZonedDateTime

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
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        repo.deleteAll()
    }


    @Test
    fun testCreateAndGet() {

        val userId = "1"
        val screeningId = "1"

        val dto = TicketDto(userId, screeningId, ZonedDateTime.now(), null)

        val id = given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/tickets")
                .then()
                .statusCode(201)

        given().auth().basic("admin", "admin")
                .get("/tickets")
                .then()
                .statusCode(200)
                .body("data.data.size()", equalTo(1))
    }


    @Test
    fun testDeleteById() {
        val ticketId = "1"
        val userId = "1"
        val screeningId = "1"

        val dto = TicketDto(userId, screeningId, ZonedDateTime.now(), ticketId)

        given().auth().basic("admin", "admin")
                .get("/tickets")
                .then()
                .statusCode(200)
                .body("data.data.size()", equalTo(0))

        val path = given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/tickets")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().auth().basic("admin", "admin")
                .get("/tickets")
                .then()
                .statusCode(200)
                .body("data.data.size()", equalTo(1))

        given().auth().basic("admin", "admin")
                .delete(path)
                .then()
                .statusCode(204)

        given().auth().basic("admin", "admin")
                .get("/tickets")
                .then()
                .statusCode(200)
                .body("data.data.size()", equalTo(0))

    }

    @Test
    fun testUpdateTicket() {
        val userId = "1"
        val screeningId = "1"

        val dto = TicketDto(userId, screeningId, ZonedDateTime.now())

        val path = given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/tickets")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().auth().basic("admin", "admin")
                .get("/tickets")
                .then()
                .statusCode(200)
                .body("data.data.size()", equalTo(1))

        given().auth().basic("admin", "admin")
                .get("/tickets")
                .then()
                .statusCode(200)
                .body("data.data[0].userId", equalTo("1"))

        val updatedDto = TicketDto("2", "2", ZonedDateTime.now(), path.split("/")[2])

        given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(updatedDto)
                .put(path)
                .then()
                .statusCode(204)

        given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .get(path)
                .then()
                .statusCode(200)
                .body("data.userId", equalTo("2"))

    }


    @Test
    fun testPatchTicket() {
        // TODO finish test
    }

    @Test
    fun test() {
        val dto = TicketDto("foo", "1")

        val resPath = given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/tickets")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().auth().basic("bar", "123")
                .get(resPath)
                .then()
                .statusCode(401)


        given().auth().basic("foo", "123")
                .get(resPath)
                .then()
                .statusCode(200)

        given().auth().basic("admin", "admin")
                .get(resPath)
                .then()
                .statusCode(200)

        given()
                .get(resPath)
                .then()
                .statusCode(401)
    }
}