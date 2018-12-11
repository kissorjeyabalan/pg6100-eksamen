package no.octopod.cinema.e2etests

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.awaitility.Awaitility.await
import org.junit.*
import org.testcontainers.containers.DockerComposeContainer
import java.io.File
import org.hamcrest.CoreMatchers.*
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
    fun testGetShows() {

        given()
                .get("/api/v1/kino/shows")
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
    fun testGetTickets() {

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
}