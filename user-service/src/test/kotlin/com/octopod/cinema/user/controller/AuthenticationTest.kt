package com.octopod.cinema.user.controller

import com.octopod.cinema.user.service.AuthenticationRepository
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.junit.AfterClass
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
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

        @AfterClass
        fun tearDown() {
            redis.dockerClient.stopContainerCmd(redis.containerId)
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
    fun testUnauthorizedAccess() {
        given().get("/error")
                .then()
                .statusCode(401)
    }

    private fun getXsrfToken() : String {
        return given()
                .options("/auth/register")
                .then()
                .statusCode(200)
                .extract().cookie("XSRF-TOKEN")
    }
}