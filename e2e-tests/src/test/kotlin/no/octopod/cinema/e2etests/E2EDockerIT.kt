package no.octopod.cinema.e2etests

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.octopod.cinema.common.dto.*
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers
import org.junit.*
import org.testcontainers.containers.DockerComposeContainer
import java.io.File
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit


class E2EDockerIT {
    companion object {

        @BeforeClass @JvmStatic
        fun checkEnvironment() {

            val travis = System.getProperty("TRAVIS") != null
            Assume.assumeTrue(!travis)
        }

        class KDockerComposeContainer(path: File): DockerComposeContainer<KDockerComposeContainer>(path)

        @ClassRule
        @JvmField
        val env = KDockerComposeContainer(File("../docker-compose.yml"))
                .withLocalCompose(true)

        private var counter = System.currentTimeMillis()

        @BeforeClass
        @JvmStatic
        fun initialize() {
            RestAssured.baseURI = "http://localhost"
            RestAssured.port = 80
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

            await()
                    .atMost(300, TimeUnit.SECONDS)
                    .pollInterval(6, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .until {

                        given()
                                .get("/api/v1/auth/user")
                                .then()
                                .statusCode(401)

                        given()
                                .get("/api/v1/kino/shows")
                                .then()
                                .statusCode(200)

                        true

                    }
        }
    }

    @Test
    fun testUnauthorizedAccess() {

        given().get("/api/v1/auth/user")
                .then()
                .statusCode(401)
    }

    @Test
    fun testAuthentication() {

        val username = "username1"
        val password = "password1"

        val authCookie = registerAuthentication(username, password)

        given()
                .cookie("SESSION", authCookie)
                .post("/api/v1/auth/logout")
                .then()
                .statusCode(204)

        given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"$username\", \"password\":\"$password\"}")
                .post("/api/v1/auth/login")
                .then()
                .statusCode(204)
    }

    @Test
    fun testGetTheaters() {

        given()
                .get("/api/v1/kino/theaters")
                .then()
                .statusCode(200)
    }

    @Test
    fun testAdminPostAndGetTheater() {

        val name = "testAdminPostAndGetTheater"
        val seats = mutableListOf("a1")
        val dto = "{\"name\":\"$name\", \"seats\":\"$seats\"}"

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/api/v1/kino/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .body("data.name", equalTo(name))
                .body("data.seats.size()", equalTo(seats.size))
    }

    @Test
    fun testAdminDeleteTheater() {

        val name = "testAdminDeleteTheater"
        val seats = mutableListOf("a1")
        val dto = "{\"name\":\"$name\", \"seats\":\"$seats\"}"

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/api/v1/kino/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .delete(path)
                .then()
                .statusCode(200)
    }

    @Test
    fun testAdminPatchTheater() {

        val name = "testAdminPatchTheater1"
        val seats = mutableListOf("a1")
        val dto = "{\"name\":\"$name\", \"seats\":\"$seats\"}"

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/api/v1/kino/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")


        val newName = "testAdminPatchTheater2"
        val newSeats = mutableListOf("b1")

        given()
                .auth().basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("{\"name\":\"$newName\", \"seats\":\"$newSeats\"}")
                .patch(path)
                .then()
                .statusCode(200)
    }

    @Test
    fun testAdminPutTheater() {

        val name = "testAdminPutTheater1"
        val seats = mutableListOf("a1")
        val dto = "{\"name\":\"$name\", \"seats\":$seats}"

        given()
                .auth()
                .basic("admin", "123")
                .get("/api/v1/auth/user")
                .then()
                .statusCode(200)

        /*val path = given()
                .auth().basic("admin", "123")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/api/v1/kino/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val newName = "testAdminPutTheater2"
        val newSeats = mutableListOf("a1")


        given()
                .auth().basic("admin", "123")
                .contentType(ContentType.JSON)
                .body("{\"name\":\"$newName\", \"seats\":\"$newSeats\"}")
                .put(path)
                .then()
                .statusCode(204)*/
    }

    @Test
    fun testGetShows() {

        given()
                .get("/api/v1/kino/shows")
                .then()
                .statusCode(200)
    }

    @Test
    fun testAdminPostAndGetShow() {

        val name = "testAdminPostAndGetShow"
        val seats = mutableListOf("a1")
        val theaterDto = TheaterDto(name = name, seats = seats)

        val theaterPath = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(theaterDto)
                .post("/api/v1/kino/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val theaterId = given()
                .auth().basic("admin", "admin")
                .get(theaterPath)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", TheaterDto::class.java)

        val movieId = 1L
        val dto = ShowDto(movieId = movieId, cinemaId = theaterId.id)

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/api/v1/kino/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .get(path)
                .then()
                .statusCode(200)
                .body("data.name", equalTo(name))
                .body("data.seats.size()", equalTo(seats.size))
    }

    @Test
    fun testAdminPostAndDeleteSeatInShow() {

        val name = "testAdminPostAndDeleteSeatInShow"
        val seats = mutableListOf("a1", "a2")
        val theaterDto = TheaterDto(name = name, seats = seats)

        val theaterPath = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(theaterDto)
                .post("/api/v1/kino/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val theaterId = given()
                .auth().basic("admin", "admin")
                .get(theaterPath)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", TheaterDto::class.java)

        val movieId = 1L
        val dto = ShowDto(movieId = movieId, cinemaId = theaterId.id)

        val showPath = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/api/v1/kino/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val showId = given()
                .auth().basic("admin", "admin")
                .get(showPath)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", TheaterDto::class.java)

        given()
                .auth().basic("admin", "admin")
                .delete("/api/v1/kino/shows/${showId.id}/seats/a2")
                .then()
                .statusCode(204)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .post("/api/v1/kino/shows/${showId.id}/seats/a2")
                .then()
                .statusCode(204)
                .extract().header("Location")
    }

    @Test
    fun testAdminDeleteShow() {

        val name = "testAdminDeleteShow"
        val seats = mutableListOf("a1")
        val theaterDto = TheaterDto(name = name, seats = seats)

        val theaterPath = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(theaterDto)
                .post("/api/v1/kino/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val theaterId = given()
                .auth().basic("admin", "admin")
                .get(theaterPath)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", TheaterDto::class.java)

        val movieId = 1L
        val dto = ShowDto(movieId = movieId, cinemaId = theaterId.id)

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/api/v1/kino/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .delete(path)
                .then()
                .statusCode(200)
    }

    @Test
    fun testAdminPutShow() {

        val name = "testAdminPutShow"
        val seats = mutableListOf("a1")
        val theaterDto = TheaterDto(name = name, seats = seats)

        val theaterPath = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(theaterDto)
                .post("/api/v1/kino/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val theaterId = given()
                .auth().basic("admin", "admin")
                .get(theaterPath)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", TheaterDto::class.java)

        val movieId = 1L
        val dto = ShowDto(movieId = movieId, cinemaId = theaterId.id)

        val newMovieId = 2L
        val newDto = ShowDto(movieId = newMovieId, cinemaId = theaterId.id)

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/api/v1/kino/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(newDto)
                .put(path)
                .then()
                .statusCode(200)
    }

    @Test
    fun testAdminPatchShow() {

        val name = "testAdminPatchShow"
        val seats = mutableListOf("a1")
        val theaterDto = TheaterDto(name = name, seats = seats)

        val theaterPath = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(theaterDto)
                .post("/api/v1/kino/theaters")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val theaterId = given()
                .auth().basic("admin", "admin")
                .get(theaterPath)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .jsonPath()
                .getObject("data", TheaterDto::class.java)

        val movieId = 1L
        val dto = ShowDto(movieId = movieId, cinemaId = theaterId.id)

        val newMovieId = 2L
        val newDto = ShowDto(movieId = newMovieId, cinemaId = theaterId.id)

        val path = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/api/v1/kino/shows")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .auth().basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body(newDto)
                .patch(path)
                .then()
                .statusCode(200)
    }

    @Test
    fun testGetMovies() {

        given()
                .get("/api/v1/movies")
                .then()
                .statusCode(200)
    }

    @Test
    fun testAdminPostAndGetMovie() {

        val movie = "Movie 1"
        val description = "Description of movie"
        val image_path = "url"
        val release_date = ZonedDateTime.now().withFixedOffsetZone().withNano(0)
        val featured = true
        val movieDto = MovieDto(
                title = movie,
                description = description,
                image_path = image_path,
                release_date = release_date,
                featured = featured
        )

        val moviePath = given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(movieDto)
                .post("/api/v1/movies")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given()
                .get(moviePath)
                .then()
                .statusCode(200)
    }

    @Test
    fun testGetOrder() {

        given()
                .get("/api/v1/order")
                .then()
                .statusCode(200)
    }

    @Test
    fun testGetTicket() {

        val username = "username2"
        val password = "password2"

        val authCookie = registerAuthentication(username, password)

        given()
                .cookie("SESSION", authCookie)
                .get("/api/v1/tickets?userId=$username")
                .then()
                .statusCode(200)
    }

    @Test
    fun testAdminPostAndGetTicket() {

        val userId = "testAdminPostAndGetTicket"
        val screeningId = "1"
        val ticketDto = TicketDto(userId, screeningId, ZonedDateTime.now().withNano(0), null)

        val ticketPath = given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(ticketDto)
                .post("/api/v1/tickets")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().auth().basic("admin", "admin")
                .get(ticketPath)
                .then()
                .statusCode(200)
                .body("data.data.size()", Matchers.equalTo(1))
    }

    @Test
    fun testAdminDeleteTicket() {

        val userId = "testAdminDeleteTicket"
        val screeningId = "1"
        val ticketDto = TicketDto(userId, screeningId, ZonedDateTime.now().withNano(0), null)

        val ticketPath = given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(ticketDto)
                .post("/api/v1/tickets")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().auth().basic("admin", "admin")
                .delete(ticketPath)
                .then()
                .statusCode(200)
    }

    @Test
    fun testAdminPutTicket() {

        val userId = "testAdminPutTicket"
        val screeningId = "1"
        val dto = TicketDto(userId, screeningId, ZonedDateTime.now().withNano(0))

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
                .body("data.data.size()", Matchers.equalTo(1))

        given().auth().basic("admin", "admin")
                .get("/tickets")
                .then()
                .statusCode(200)
                .body("data.data[0].userId", Matchers.equalTo("1"))

        val newUserId = "testAdminPutTicket-new"
        val newScreeningId = "2"
        val updatedDto = TicketDto(newUserId, newScreeningId, ZonedDateTime.now().withNano(0), path.split("/")[2])

        given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(updatedDto)
                .put(path)
                .then()
                .statusCode(204)
    }

    @Test
    fun testAdminPatchTicket() {

        val userId = "testAdminPatchTicket"
        val screeningId = "1"

        val dto = TicketDto(userId, screeningId, ZonedDateTime.now().withNano(0))

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
    }

    @Test
    fun testPostAndGetUser() {

        val phone = "22345678"
        val email = "test@test.abc"
        val username = "username3"
        val password = "password3"

        val authCookie = registerAuthentication(phone, password)

        given()
                .cookie("SESSION", authCookie)
                .contentType(ContentType.JSON)
                .body("{\"phone\":\"$phone\", \"email\":\"$email\", \"name\":\"$username\"}")
                .post("/api/v1/users")
                .then()
                .statusCode(201)

        given()
                .cookie("SESSION", authCookie)
                .get("/api/v1/users/$phone")
                .then()
                .statusCode(200)
    }

    @Test
    fun testPostAndPutUser() {

        val phone = "12345698"
        val email = "test@test.abc"
        val username = "username4"
        val password = "password4"

        val authCookie = registerAuthentication(phone, password)

        given()
                .cookie("SESSION", authCookie)
                .contentType(ContentType.JSON)
                .body("{\"phone\":\"$phone\", \"email\":\"$email\", \"name\":\"$username\"}")
                .post("/api/v1/users")
                .then()
                .statusCode(201)

        val newEmail = "replacement4@replacement.abc"
        val newUsername = "username4-1"

        given()
                .cookie("SESSION", authCookie)
                .contentType(ContentType.JSON)
                .body("{\"phone\":\"$phone\", \"email\":\"$newEmail\", \"name\":\"$newUsername\"}")
                .put("/api/v1/users/$phone")
                .then()
                .statusCode(204)
    }

    @Test
    fun testPostAndPatchUser() {

        val phone = "12345679"
        val email = "test@test.abc"
        val username = "username5"
        val password = "password5"

        val authCookie = registerAuthentication(phone, password)

        given()
                .cookie("SESSION", authCookie)
                .contentType(ContentType.JSON)
                .body("{\"phone\":\"$phone\", \"email\":\"$email\", \"name\":\"$username\"}")
                .post("/api/v1/users")
                .then()
                .statusCode(201)

        val newEmail = "replacement5@replacement.abc"
        val newUsername = "username5-1"

        given()
                .cookie("SESSION", authCookie)
                .contentType("application/merge-patch+json")
                .body("{\"email\":\"$newEmail\", \"name\":\"$newUsername\"}")
                .patch("/api/v1/users/$phone")
                .then()
                .statusCode(204)
    }

    fun registerAuthentication(username: String? = null, password: String? = null): String {

        var userId: String
        var userPwd: String

        if (username.isNullOrBlank() && password.isNullOrBlank()) {
            userId =  counter.toString()
            userPwd = counter.toString()
        } else {
            userId = username!!
            userPwd = password!!
        }

        val session = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"$userId\", \"password\":\"$userPwd\"}")
                .post("/api/v1/auth/register")
                .then()
                .statusCode(204)
                .header("Set-Cookie", not(equalTo(null)))
                .extract().cookie("SESSION")

        return session
    }

    @Test
    fun testAdminGetAnotherUser() {

        val phone = "testAdminGetAnotherUser"
        val email = "test@test.abc"
        val username = "username6"
        val password = "password6"

        val authCookie = registerAuthentication(phone, password)

        given()
                .cookie("SESSION", authCookie)
                .contentType(ContentType.JSON)
                .body("{\"phone\":\"$phone\", \"email\":\"$email\", \"name\":\"$username\"}")
                .post("/api/v1/users")
                .then()
                .statusCode(201)

        given()
                .auth().basic("admin", "admin")
                .get("/api/v1/users/$phone")
                .then()
                .statusCode(200)
    }

    @Test
    fun testAdminGetAll() {

        given()
                .auth().basic("admin", "123")
                .get("/api/v1/users")
                .then()
                .statusCode(200)
    }

    @Test
    fun testAdminDeleteUser() {

        val phone = "testAdminGetUser"
        val email = "test@test.abc"
        val username = "username6"
        val password = "password6"

        val authCookie = registerAuthentication(phone, password)

        given()
                .cookie("SESSION", authCookie)
                .contentType(ContentType.JSON)
                .body("{\"phone\":\"$phone\", \"email\":\"$email\", \"name\":\"$username\"}")
                .post("/api/v1/users")
                .then()
                .statusCode(201)

        given()
                .auth().basic("admin", "admin")
                .delete("/api/v1/users/$phone")
                .then()
                .statusCode(204)
    }

    @Test
    fun testAdminDeleteNonExistentUser() {

        val phone = "testAdminDeleteNonExistentUser"

        given()
                .auth().basic("admin", "admin")
                .delete("/api/v1/users/$phone")
                .then()
                .statusCode(404)
    }
}