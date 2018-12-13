package no.octopod.cinema.ticket.api

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
import org.springframework.test.context.ActiveProfiles
import java.time.ZonedDateTime

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
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
        val seatNum = "a1"

        val dto = TicketDto(userId, screeningId, ZonedDateTime.now().withNano(0), seat = seatNum)

        given().auth().basic("admin", "admin")
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
    fun testCreateAndFailWithNullVariabel() {

        val userId = null
        val screeningId = null
        val dto = TicketDto(userId, screeningId)

        given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/tickets")
                .then()
                .statusCode(400)
                .extract().header("Location")

        given().auth().basic("admin", "admin")
                .get("/tickets")
                .then()
                .statusCode(200)
                .body("data.data.size()", CoreMatchers.equalTo(0))
    }

    @Test
    fun testDeleteById() {
        val ticketId = "1"
        val userId = "1"
        val screeningId = "1"

        val dto = TicketDto(userId, screeningId, ZonedDateTime.now().withNano(0), ticketId)

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
        val seatNum = "1a"

        val dto = TicketDto(userId, screeningId, ZonedDateTime.now().withNano(0), seatNum)

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

        val updatedDto = TicketDto("2", "2", ZonedDateTime.now().withNano(0), "2b", id = path.split("/")[2])

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
    fun testUpdateTicketWithNonExistentId() {

        val dto = TicketDto("2", "2", ZonedDateTime.now().withNano(0), seat = "a1", id = "99")

        given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .put("tickets/99")
                .then()
                .statusCode(404)
    }

    @Test
    fun testDeleteTicketWithNonExistentId() {

        val dto = TicketDto("2", "2", ZonedDateTime.now().withNano(0), "99")

        given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .delete("tickets/99")
                .then()
                .statusCode(404)
    }

    @Test
    fun testPatchUserIdOnTicket() {
        val userId = "1"
        val screeningId = "1"
        val seatNum = "a1"

        val dto = TicketDto(userId, screeningId, ZonedDateTime.now().withNano(0), seat = seatNum)

        val path = given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/tickets")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val newUserId = "2"
        val body = "{\"userId\":\"$newUserId\"}"

        given().auth().basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body(body)
                .patch(path)
                .then()
                .statusCode(204)

        given().auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .body("data.userId", CoreMatchers.equalTo(newUserId))
                .body("data.screeningId", CoreMatchers.equalTo(dto.screeningId))

    }

    @Test
    fun testPatchScreeningIdOnTicket() {
        val userId = "1"
        val screeningId = "1"
        val seatNum = "a1"

        val dto = TicketDto(userId, screeningId, ZonedDateTime.now().withNano(0), seat = seatNum)

        val path = given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/tickets")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val newScreeningId = "2"
        val body = "{\"screeningId\":\"$newScreeningId\"}"

        given().auth().basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body(body)
                .patch(path)
                .then()
                .statusCode(204)

        given().auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .body("data.userId", CoreMatchers.equalTo(dto.userId))
                .body("data.screeningId", CoreMatchers.equalTo(newScreeningId))
    }

    @Test
    fun testAuth() {
        val dto = TicketDto("foo", "1", seat = "a2")

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

    @Test
    fun testGetAsAdminAndNotAsAdmin() {

        given().auth().basic("admin", "admin")
                .get("/tickets")
                .then()
                .statusCode(not(401))

        given().auth().basic("foo", "123")
                .get("/tickets")
                .then()
                .statusCode((401))
    }

    @Test
    fun testPostAsAdminAndNotAsAdmin() {

        val userId = "1"
        val screeningId = "1"

        val dto = TicketDto(userId, screeningId, ZonedDateTime.now().withNano(0))

        given().auth().basic("admin", "admin")
                .body(dto)
                .post("/tickets")
                .then()
                .statusCode(not(403))

        given().auth().basic("foo", "123")
                .body(dto)
                .post("/tickets")
                .then()
                .statusCode((403))
    }

    @Test
    fun testPutAsAdminAndNotAsAdmin() {

        val userId = "1"
        val screeningId = "1"

        val dto = TicketDto(userId, screeningId, ZonedDateTime.now().withNano(0))

        given().auth().basic("admin", "admin")
                .body(dto)
                .put("/tickets")
                .then()
                .statusCode(not(403))

        given().auth().basic("foo", "123")
                .body(dto)
                .put("/tickets")
                .then()
                .statusCode((403))
    }
}