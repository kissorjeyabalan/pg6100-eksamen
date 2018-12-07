package no.octopod.cinema.auth.controller

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.octopod.cinema.auth.dto.LoginDto
import no.octopod.cinema.auth.service.AuthenticationRepository
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.GenericContainer

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [AuthenticationTest.Companion.Initializer::class])
class AuthenticationTest {
    @Autowired private lateinit var authRepo: AuthenticationRepository
    @LocalServerPort private var port = 0

    companion object {
        class KGenericContainer(image: String) : GenericContainer<KGenericContainer>(image)

        @ClassRule
        @JvmField
        val redis = KGenericContainer("redis:latest")
                .withExposedPorts(6379)

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(cAppContext: ConfigurableApplicationContext) {
                val host = redis.containerIpAddress
                val port = redis.getMappedPort(6379)

                TestPropertyValues.of(
                        "spring.redis.host=$host",
                        "spring.redis.port=$port")
                        .applyTo(cAppContext.environment)

            }
        }
    }

    @Before
    fun initialize() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        authRepo.deleteAll()
    }

    @Test
    fun testUserIsUnauthorized() {
        given().get("/error")
                .then()
                .statusCode(401)
    }

    @Test
    fun testRegister() {
        given().contentType(ContentType.JSON)
                .body(LoginDto("userId", "password"))
                .then()
                .statusCode(204)
                .header("Set-Cookie", not(equalTo(null)))
    }

    @Test
    fun testLogin() {
        val userId = "12345678"
        val pwd = "123"

        checkAuthenticatedCookie("notACookie", 401)

        val cookie = registerUser(userId, pwd)

        given().get("/user")
                .then()
                .statusCode(401)

        given().cookie("SESSION", cookie)
                .get("/user")
                .then()
                .statusCode(200)
                .body("name", equalTo(userId))
                .body("roles", hasItem("ROLE_USER"))

        val basic = given().auth().basic(userId, pwd)
                .get("/user")
                .then()
                .statusCode(200)
                .cookie("SESSION")
                .body("name", equalTo(userId))
                .body("roles", hasItem("ROLE_USER"))
                .extract().cookie("SESSION")

        assertNotEquals(cookie, basic)
        checkAuthenticatedCookie(basic, 200)

        val login = given().contentType(ContentType.JSON)
                .body(LoginDto(userId, pwd))
                .post("/login")
                .then()
                .statusCode(204)
                .cookie("SESSION")
                .extract().cookie("SESSION")

        assertNotEquals(login, cookie)
        assertNotEquals(login, basic)
        checkAuthenticatedCookie(login, 200)
    }

    @Test
    fun testInvalidLogin() {
        val userId = "12345678"
        val pwd = "123"

        val invalid = given().contentType(ContentType.JSON)
                .body(LoginDto(userId, pwd))
                .post("/login")
                .then()
                .statusCode(400)
                .extract().cookie("SESSION")

        checkAuthenticatedCookie(invalid, 401)

        registerUser(userId, pwd)

        val authenticated = given().contentType(ContentType.JSON)
                .body(LoginDto(userId, pwd))
                .post("/login")
                .then()
                .statusCode(204)
                .extract().cookie("SESSION")

        checkAuthenticatedCookie(authenticated, 200)
    }

    @Test
    fun testLogOut() {
        val userId = "12345678"
        val pwd = "123"

        val authCookie = registerUser(userId, pwd)
        checkAuthenticatedCookie(authCookie, 200)

        given().cookie("SESSION", authCookie)
                .post("/logout")
                .then()
                .statusCode(204)

        checkAuthenticatedCookie(authCookie, 401)
    }

    @Test
    fun testDuplicateUser() {
        val userId = "12345678"
        val pwd = "123"

        registerUser(userId, pwd)
        given().contentType(ContentType.JSON)
                .body(LoginDto(userId, pwd))
                .post("/register")
                .then()
                .statusCode(400)
    }

    @Test
    fun testIllegalLoginValue() {
        given().contentType(ContentType.JSON)
                .body(LoginDto(null, ""))
                .post("/login")
                .then()
                .statusCode(400)
    }

    @Test
    fun testIllegalRegisterValue() {
        given().contentType(ContentType.JSON)
                .body(LoginDto(null, ""))
                .post("/register")
                .then()
                .statusCode(400)
    }


    // TODO: Add source
    private fun registerUser(userId: String, password: String): String {
        val sessionCookie = given().contentType(ContentType.JSON)
                .body(LoginDto(userId, password))
                .post("/register")
                .then()
                .statusCode(204)
                .header("Set-Cookie", not(equalTo(null)))
                .extract().cookie("SESSION")

        return sessionCookie
    }

    private fun checkAuthenticatedCookie(cookie: String, expectedStatusCode: Int) {
        given().cookie("SESSION", cookie)
                .get("/user")
                .then()
                .statusCode(expectedStatusCode)
    }
}